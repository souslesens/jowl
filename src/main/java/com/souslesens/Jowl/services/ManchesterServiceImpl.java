package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.jenaTripleParser;

import org.apache.jena.rdf.model.Statement;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import org.apache.jena.rdf.model.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@Service
public class ManchesterServiceImpl implements ManchesterService {

    @Autowired
    VirtuosoService virtuosoService;

    @Override
    public OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException {

        OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
        OWLOntology owlOntology = null;
        try {
            owlOntology = virtuosoService.readOntologyFromVirtuoso(graphName);
        } catch (OWLOntologyStorageException | NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return null;
        }
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
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom) throws OWLOntologyStorageException {
        ArrayList<jenaTripleParser> triples = new ArrayList<>();
        Ontology owl = OntManagers.createManager().createOntology();

        owl.addAxiom(axiom);

        Model jena = owl.asGraphModel();

        Set<Statement> statements = jena.listStatements().toSet();

        System.out.println("Statements: ");
        for (Statement statement : statements) {
            //ignore rdfs type statements
            if (statement.getPredicate().toString().contains("type")) {
                continue;
            }
            System.out.println(statement);
            jenaTripleParser triple = new jenaTripleParser();
            triple.setSubject(statement.getSubject().toString());
            triple.setPredicate(statement.getPredicate().toString());
            triple.setObject(statement.getObject().toString());
            triples.add(triple);
        }
        return triples;
    }

    private BidirectionalShortFormProvider getShortFormProvider(OWLOntologyManager owlManager, OWLOntology owlOntology) {

        Set<OWLOntology> ontologies = owlManager.getOntologies();
        ShortFormProvider sfp = new ManchesterOWLSyntaxPrefixNameShortFormProvider(
                owlManager.getOntologyFormat(owlOntology));
        BidirectionalShortFormProvider shortFormProvider = new BidirectionalShortFormProviderAdapter(
                ontologies, sfp);
        return shortFormProvider;
    }
}
