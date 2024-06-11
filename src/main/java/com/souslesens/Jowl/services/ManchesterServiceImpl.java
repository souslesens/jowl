package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.model.jenaTripleParser;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.formats.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class ManchesterServiceImpl implements ManchesterService {

    @Autowired
    VirtuosoService virtuosoService;

    @Autowired
    HermitReasonerService hermitReasonerService;

    @Override
    public OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException, ParsingAxiomException, NoVirtuosoTriplesException {

        OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
        OWLOntology owlOntology = virtuosoService.readOntologyFromVirtuoso(graphName);

        if (owlOntology == null) {
            System.out.println("Error reading ontology from Virtuoso");
            return null;
        }
        System.out.println("Ontology: " + owlOntology);

        OWLDataFactory dataFactory = owlManager.getOWLDataFactory();
        ShortFormProvider sfp =
                new AnnotationValueShortFormProvider(Arrays.asList(dataFactory.getRDFSLabel()),
                        Collections.<OWLAnnotationProperty, List<String>>emptyMap(), owlManager);
        BidirectionalShortFormProvider shortFormProvider =
                new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), sfp);

        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setDefaultOntology(owlOntology);

        parser.setStringToParse(input);

        ShortFormEntityChecker checker = new ShortFormEntityChecker(shortFormProvider);

        parser.setOWLEntityChecker(checker);

        OWLAxiom axiom;

        try {
            axiom = parser.parseAxiom();
            System.out.println("Axiom:" + axiom);
            return axiom;
        } catch (Exception e) {
            throw new ParsingAxiomException(e.getMessage());
        }

    }

    @Override
    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom) {
        ArrayList<jenaTripleParser> triples = new ArrayList<>();
        Ontology owl = OntManagers.createManager().createOntology();

        owl.addAxiom(axiom);

        Model jena = owl.asGraphModel();

        Set<Statement> statements = jena.listStatements().toSet();

        System.out.println("Statements: ");
        for (Statement statement : statements) {
            //ignore rdfs type statements
//            if (statement.getPredicate().toString().contains("type")) {
//                continue;
//            }
            System.out.println(statement);
            jenaTripleParser triple = new jenaTripleParser();
            if (statement.getSubject().isAnon()) {
                triple.setSubject("_" + statement.getSubject().getId().getLabelString());
            } else {
                triple.setSubject(statement.getSubject().getURI());
            }

            // Set the predicate (URI is always not a blank node)
            triple.setPredicate(statement.getPredicate().getURI());

            // Check if the object is a blank node and modify accordingly
            if (statement.getObject().isAnon()) {
                triple.setObject("_" + ((Resource) statement.getObject()).getId().getLabelString());
            } else {
                triple.setObject(statement.getObject().toString().replace("[OntObject]", ""));
            }

            triples.add(triple);
        }
        return triples;
    }

    @Override
    public boolean checkManchesterAxiomConsistency(String graphName, OWLAxiom axiom) throws OWLOntologyCreationException, NoVirtuosoTriplesException {
        OWLOntology owlOntology = virtuosoService.readOntologyFromVirtuoso(graphName);

        if (owlOntology == null) {
            System.out.println("Error reading ontology from Virtuoso");
            return false;
        }

        owlOntology.addAxiom(axiom);

        return hermitReasonerService.getConsistency(owlOntology);

    }

    @Override
    public String triplesToManchester(String graphName, jenaTripleParser[] triples) throws OWLOntologyCreationException, NoVirtuosoTriplesException, AuthenticationException, MalformedChallengeException, IOException, URISyntaxException {

        Set<String> uris = new HashSet<>();

        for (jenaTripleParser triple : triples) {
            if (!triple.getObject().startsWith("_")) uris.add(triple.getObject());
            if (!triple.getSubject().startsWith("_")) uris.add(triple.getSubject());
        }

        //fetch from sparql virtuoso triples of ns type of the uris, inject them into the ontology then inject the original triples into the ontolgoy also

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ?subject ?predicate ?object WHERE { GRAPH <").append(graphName).append("> { ?subject ?predicate ?object . VALUES ?subject {");

        for (String uri : uris) {
            queryBuilder.append(" <").append(uri).append(">");
        }
        queryBuilder.append(" } FILTER(?predicate = <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) }}");

        String queryString = queryBuilder.toString();
        System.out.println(queryString);


        JSONArray typeTriples = virtuosoService.querySparql(queryString).getJSONArray("bindings");
        System.out.println(typeTriples);
        //Ontology o = virtuosoService.readOntologyFromVirtuoso(graphName);

        OntologyManager m = OntManagers.createManager();
        Ontology o = m.createOntology(IRI.create(graphName));

        System.out.println("axiom count:" + o.getAxiomCount());

//        for (int i = 0; i < typeTriples.length(); i++) {
//            JSONObject triple = typeTriples.getJSONObject(i);
//            Resource subjectResource = ResourceFactory.createResource(triple.getJSONObject("subject").getString("value"));
//            Property predicateProperty = ResourceFactory.createProperty(triple.getJSONObject("predicate").getString("value"));
//            RDFNode objectNode = ResourceFactory.createResource(triple.getJSONObject("object").getString("value"));
//
//            o.asGraphModel().add(subjectResource, predicateProperty, objectNode);
//        }
        System.out.println("axiom count:" + o.getAxiomCount());

        Map<String, Resource> blankNodeMap = new HashMap<>();

        for (jenaTripleParser triple : triples) {
            Resource subjectResource;
            RDFNode objectNode;

            // Handle subject
            if (triple.getSubject().startsWith("_")) {
                subjectResource = blankNodeMap.computeIfAbsent(triple.getSubject(), k -> ResourceFactory.createResource());
            } else {
                subjectResource = ResourceFactory.createResource(triple.getSubject());
            }

            // Handle predicate
            Property predicateProperty = ResourceFactory.createProperty(triple.getPredicate());

            // Handle object
            if (triple.getObject().startsWith("_")) {
                objectNode = blankNodeMap.computeIfAbsent(triple.getObject(), k -> ResourceFactory.createResource());
            } else {
                objectNode = ResourceFactory.createResource(triple.getObject());
            }

            // Add the triple to the model
            o.asGraphModel().add(subjectResource, predicateProperty, objectNode);
        }
        System.out.println("axiom count:" + o.getAxiomCount());
        System.out.println("axiom: " + o.getLogicalAxioms());

        List<String> manchesterAxioms =  new ArrayList<>();

        for (OWLLogicalAxiom axiom: o.getLogicalAxioms()) {
            String axiomType = getAxiomType(triples);
            System.out.println("axiom type: "+axiomType);
            String axiomString = convertToManchesterSyntax(axiom);
            System.out.println("axiom: " + axiomString);
            manchesterAxioms.add(extractAxiomString(axiomString, axiomType ));
        }

        System.out.println(manchesterAxioms);


        return String.valueOf(manchesterAxioms);
    }

    public String convertToManchesterSyntax(OWLAxiom axiom) {
        //OWLOntologyLoaderConfiguration loaderConfig = new OWLOntologyLoaderConfiguration();
        //loaderConfig = loaderConfig.setLoadAnnotationAxioms(false); // Skip loading annotation axioms
        ManchesterOWLSyntaxOntologyFormat format = new ManchesterOWLSyntaxOntologyFormat();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;

        try {

            ontology = manager.createOntology();
            manager.addAxiom(ontology, axiom);
            StringDocumentTarget documentTarget = new StringDocumentTarget();
            manager.saveOntology(ontology, format, documentTarget);
            return documentTarget.toString();
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String extractAxiomString(String serializedOntology, String axiomType) {
        serializedOntology = serializedOntology.replaceAll("\\n", "").replaceAll("\\s+", " ").trim();
        if (axiomType.equals("DisjointUnion")) {
            String searchToken = "DisjointUnionOf:";
            int startIndex = serializedOntology.indexOf(searchToken);
            if (startIndex != -1) {
                startIndex += searchToken.length();
                int endIndex = serializedOntology.indexOf("Class:", startIndex);
                if (endIndex == -1) {
                    endIndex = serializedOntology.length();
                }
                return serializedOntology.substring(startIndex, endIndex).trim();
            }
        } else if (axiomType.equals(ManchesterOWLSyntax.DISJOINT_WITH.keyword())) {

            int startIndex = serializedOntology.indexOf(axiomType);
            if (startIndex != -1) {
                startIndex += axiomType.length();
                int endIndex = serializedOntology.indexOf("Class:", startIndex);
                if (endIndex == -1) {
                    endIndex = serializedOntology.length();
                }
                return serializedOntology.substring(startIndex, endIndex).trim();
            }
        } else if (axiomType.equals(ManchesterOWLSyntax.SUBCLASS_OF.keyword())) {
            int startIndex = serializedOntology.indexOf(axiomType);
            if (startIndex != -1) {
                startIndex += axiomType.length();
                int endIndex = serializedOntology.indexOf("Class:", startIndex);
                if (endIndex == -1) {
                    endIndex = serializedOntology.length();
                }
                return  serializedOntology.substring(startIndex, endIndex).trim();
            }
        } else if (axiomType.equals(ManchesterOWLSyntax.EQUIVALENT_CLASSES.keyword())) {
            int startIndex = serializedOntology.indexOf(axiomType);
            if (startIndex != -1) {
                startIndex += axiomType.length();
                int endIndex = serializedOntology.indexOf("Class:", startIndex);
                if (endIndex == -1) {
                    endIndex = serializedOntology.length();
                }
                return serializedOntology.substring(startIndex, endIndex).trim();
            }
        } else {
            return serializedOntology;
        }
        return serializedOntology;
    }

    private String getAxiomType(jenaTripleParser[] triples) {
        for (jenaTripleParser tripleParser: triples ) {
            String predicateURI = tripleParser.getPredicate();
            if (predicateURI.equals(RDFS.subClassOf.getURI())) {
                return ManchesterOWLSyntax.SUBCLASS_OF.keyword();
            } else
            if (predicateURI.equals(OWL.equivalentClass.getURI())) {
                return ManchesterOWLSyntax.EQUIVALENT_CLASSES.keyword();
            } else
            if (predicateURI.equals(OWL.disjointWith.getURI())) {
                return ManchesterOWLSyntax.DISJOINT_WITH.keyword();
            }
        }
        return null;
    }

}
