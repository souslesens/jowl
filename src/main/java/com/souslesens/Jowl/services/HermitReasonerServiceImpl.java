package com.souslesens.Jowl.services;

import org.json.JSONObject;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

@Service
public class HermitReasonerServiceImpl implements HermitReasonerService {

    @Autowired
    VirtuosoService virtuosoService;

    @Override
    public String getUnsatisfaisableClasses(String filePath, String Url, String grapheName) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && grapheName == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (grapheName == null && !filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            } else {
                ontology = virtuosoService.readOntologyFromVirtuoso(grapheName, true);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration config = new Configuration();
        Reasoner hermit = new Reasoner(config, ontology);
        Node<OWLClass> unsatisfiableNodes = hermit.getUnsatisfiableClasses();

        String[] iriStrings = unsatisfiableNodes.getEntities().stream().map(OWLClass::getIRI).map(IRI::toString).toArray(String[]::new);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unsatisfiable", iriStrings);
        return jsonObject.toString();

    }

    @Override
    public String getUnsatisfaisableClasses(String ontologyContentDecoded64) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
        //set silent imports
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        manager.setOntologyLoaderConfiguration(config);
        OWLOntology ontology = null;
        try {
            if (instream.available() > 0) {
                OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
                ontology = manager.loadOntologyFromOntologyDocument(documentSource);
            }
        } catch (OWLOntologyCreationException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration c = new Configuration();
        Reasoner hermit = new Reasoner(c, ontology);
        Node<OWLClass> unsatisfiableNodes = hermit.getUnsatisfiableClasses();

        String[] iriStrings = unsatisfiableNodes.getEntities().stream().map(OWLClass::getIRI).map(IRI::toString).toArray(String[]::new);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unsatisfiable", iriStrings);
        return jsonObject.toString();
    }

    @Override
    public String getConsistency(String filePath, String Url, String graphName) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && graphName == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (graphName == null && !filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            } else {
                ontology = virtuosoService.readOntologyFromVirtuoso(graphName, true);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration config = new Configuration();
        Reasoner hermit = new Reasoner(config, ontology);
        return (new JSONObject()).put("Consistency", hermit.isConsistent()).toString();
    }

    @Override
    public String getConsistency(String ontologyContentDecoded64)
            throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
        //set silent imports
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        manager.setOntologyLoaderConfiguration(config);
        OWLOntology ontology = null;
        try {
            if (instream.available() > 0) {
                OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
                ontology = manager.loadOntologyFromOntologyDocument(documentSource);
            }
        } catch (OWLOntologyCreationException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration c = new Configuration();
        Reasoner hermit = new Reasoner(c, ontology);
        return (new JSONObject()).put("Consistency", hermit.isConsistent()).toString();
    }

    @Override
    public boolean getConsistency(OWLOntology ontology) {

        Reasoner hermit = new Reasoner(new Configuration(), ontology);
        return hermit.isConsistent();
    }

    @Override
    public String getInferences(String filePath, String Url, List<String> ListOfValues, String graphName) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && graphName == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                System.out.println("reading ontology from url");
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (graphName == null && !filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            } else {
                ontology = virtuosoService.readOntologyFromVirtuoso(graphName, true);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        System.out.println("configuring reasoner");
        Configuration config = new Configuration();
        Reasoner hermit = new Reasoner(config, ontology);
        hermit.precomputeInferences(InferenceType.values());

        InferredOntologyGenerator generator = new InferredOntologyGenerator(hermit);

        for (String value : ListOfValues) {
            try {
                boolean generatorAdded = false;
                if (value.contentEquals("CustomInferredIntersectionOfAxiomGenerator()")   && !generatorAdded ) {
                    generator.addGenerator( new CustomInferredIntersectionOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredEquivalentClassesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredEquivalentClassesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomSameIndividualAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomSameIndividualAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredUnionOfAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredUnionOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDisjointClassesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredDisjointClassesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDifferentIndividualAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredDifferentIndividualAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredHasValueAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredHasValueAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredInverseObjectPropertiesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredInverseObjectPropertiesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredAllValuesFromAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredAllValuesFromAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredSameValueSomeValuesFromAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDomainAndRangeAxiomGenerator()") && !generatorAdded)  {
                    generator.addGenerator( new CustomInferredDomainAndRangeAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredClassAssertionAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredClassAssertionAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredSubClassAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubClassAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredDataPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredEquivalentDataPropertiesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                    generatorAdded = true;

                }else if (value.contentEquals("InferredEquivalentObjectPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredSubObjectPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                    generatorAdded = true;

                }else if (value.contentEquals("InferredSubDataPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredObjectPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredPropertyAssertionGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredPropertyAssertionGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredComplementOfAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredComplementOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("All")) {
                    generator.addGenerator(new CustomInferredEquivalentClassesAxiomGenerator() );
                    generator.addGenerator(new ReasonerServiceImpl.CustomSameIndividualAxiomGenerator());
                    generator.addGenerator(new CustomInferredDifferentIndividualAxiomGenerator());
                    generator.addGenerator(new CustomInferredIntersectionOfAxiomGenerator());
                    generator.addGenerator(new CustomInferredUnionOfAxiomGenerator());
                    generator.addGenerator(new CustomInferredDisjointClassesAxiomGenerator());
                    generator.addGenerator(new CustomInferredHasValueAxiomGenerator());
                    generator.addGenerator(new CustomInferredInverseObjectPropertiesAxiomGenerator() );
                    generator.addGenerator(new CustomInferredAllValuesFromAxiomGenerator());
                    generator.addGenerator(new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                    generator.addGenerator(new CustomInferredDomainAndRangeAxiomGenerator());
                    generator.addGenerator( new InferredSubClassAxiomGenerator());
                    generator.addGenerator( new InferredClassAssertionAxiomGenerator());
                    generator.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                    generator.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                    generator.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                    generator.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                    generator.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                    generator.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                    generator.addGenerator( new InferredPropertyAssertionGenerator());
                    generator.addGenerator(new CustomInferredComplementOfAxiomGenerator()); // Generate owl:ComplementOf

                    break;
                }



            } catch (Exception e) {
                e.printStackTrace(); // Handle any exceptions that may occur
            }
        }

        System.out.println("generators added");


        OWLOntology inferredOntology = manager.createOntology();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        generator.fillOntology(dataFactory, inferredOntology);

        // Filter out original axioms and keep only inferred ones

        Set<OWLAxiom> originalAxioms = ontology.getAxioms();

        Set<OWLAxiom> inferredAxioms = new HashSet<>(inferredOntology.getAxioms());

        System.out.println(inferredAxioms.size());

        inferredAxioms.removeAll(originalAxioms);

        System.out.println(inferredAxioms.size());

        StringBuilder sb = new StringBuilder();
        // Add the inferred axioms to the list

        for (OWLAxiom axiom : inferredAxioms) {
            sb.append(axiom.toString());
        }

        String axiomsString = sb.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inference", axiomsString);

        return jsonObject.toString();

    }

    @Override
    public String getInferences(String ontologyContentDecoded64, List<String> ListOfValues)
            throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
        //set silent imports
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        manager.setOntologyLoaderConfiguration(config);
        OWLOntology ontology = null;
        try {
            if (instream.available() > 0) {
                OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
                ontology = manager.loadOntologyFromOntologyDocument(documentSource);
            }
        } catch (OWLOntologyCreationException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration c = new Configuration();
        Reasoner hermit = new Reasoner(c, ontology);
        hermit.precomputeInferences(InferenceType.values());

        InferredOntologyGenerator generator = new InferredOntologyGenerator(hermit);

        for (String value : ListOfValues) {
            try {
                boolean generatorAdded = false;
                if (value.contentEquals("CustomInferredIntersectionOfAxiomGenerator()")   && !generatorAdded ) {
                    generator.addGenerator( new CustomInferredIntersectionOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredEquivalentClassesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredEquivalentClassesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomSameIndividualAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomSameIndividualAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredUnionOfAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredUnionOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDisjointClassesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredDisjointClassesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDifferentIndividualAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredDifferentIndividualAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredHasValueAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredHasValueAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredInverseObjectPropertiesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredInverseObjectPropertiesAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredAllValuesFromAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredAllValuesFromAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredSameValueSomeValuesFromAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDomainAndRangeAxiomGenerator()") && !generatorAdded)  {
                    generator.addGenerator( new CustomInferredDomainAndRangeAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredClassAssertionAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredClassAssertionAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredSubClassAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubClassAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredDataPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredEquivalentDataPropertiesAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                    generatorAdded = true;

                }else if (value.contentEquals("InferredEquivalentObjectPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredSubObjectPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                    generatorAdded = true;

                }else if (value.contentEquals("InferredSubDataPropertyAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredObjectPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("InferredPropertyAssertionGenerator()") && !generatorAdded) {
                    generator.addGenerator( new InferredPropertyAssertionGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("CustomInferredComplementOfAxiomGenerator()") && !generatorAdded) {
                    generator.addGenerator( new CustomInferredComplementOfAxiomGenerator());
                    generatorAdded = true;
                }else if (value.contentEquals("All")) {
                    generator.addGenerator(new CustomInferredEquivalentClassesAxiomGenerator() );
                    generator.addGenerator(new ReasonerServiceImpl.CustomSameIndividualAxiomGenerator());
                    generator.addGenerator(new CustomInferredDifferentIndividualAxiomGenerator());
                    generator.addGenerator(new CustomInferredIntersectionOfAxiomGenerator());
                    generator.addGenerator(new CustomInferredUnionOfAxiomGenerator());
                    generator.addGenerator(new CustomInferredDisjointClassesAxiomGenerator());
                    generator.addGenerator(new CustomInferredHasValueAxiomGenerator());
                    generator.addGenerator(new CustomInferredInverseObjectPropertiesAxiomGenerator() );
                    generator.addGenerator(new CustomInferredAllValuesFromAxiomGenerator());
                    generator.addGenerator(new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                    generator.addGenerator(new CustomInferredDomainAndRangeAxiomGenerator());
                    generator.addGenerator( new InferredSubClassAxiomGenerator());
                    generator.addGenerator( new InferredClassAssertionAxiomGenerator());
                    generator.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                    generator.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                    generator.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                    generator.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                    generator.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                    generator.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                    generator.addGenerator( new InferredPropertyAssertionGenerator());
                    generator.addGenerator(new CustomInferredComplementOfAxiomGenerator()); // Generate owl:ComplementOf

                    break;
                }else {
                    break;
                }
                if (generatorAdded) {
                    continue;
                }



            } catch (Exception e) {
                e.printStackTrace(); // Handle any exceptions that may occur
            }
        }

        OWLOntology inferredOntology = manager.createOntology();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        generator.fillOntology(dataFactory, inferredOntology);

        Set<OWLAxiom> originalAxioms = ontology.getAxioms();
        Set<OWLAxiom> inferredAxioms = new HashSet<>(inferredOntology.getAxioms());
        System.out.println(inferredAxioms.size());
        inferredAxioms.removeAll(originalAxioms);
        System.out.println(inferredAxioms.size());

        StringBuilder sb = new StringBuilder();
        // Add the inferred axioms to the list
        for (OWLAxiom axiom : inferredAxioms) {
            sb.append(axiom.toString());

        }
        String axiomsString = sb.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inference", axiomsString);

        return jsonObject.toString();

    }

    public Map<String, String> getParameteresInferenceMethod() {
        Map<String, String> hashMap = new HashMap<>();
        // Add key-value pairs to the HashMap
        hashMap.put("equivalentClass", "CustomInferredEquivalentClassesAxiomGenerator()"); // Covers owl:equivalentClass Inferences
        hashMap.put("sameIndividual", "CustomSameIndividualAxiomGenerator()"); // Covers Owl SameAs
        hashMap.put("IntersectionOf", "CustomInferredIntersectionOfAxiomGenerator()"); // Covers Owl:Intersection:Of Inferences
        hashMap.put("UnionOf", "CustomInferredUnionOfAxiomGenerator()"); // Covers Owl:UnionOf inferences
        hashMap.put("DisjointClasses", "CustomInferredDisjointClassesAxiomGenerator()"); // Covers Owl:DisjointClasses Inferences
        hashMap.put("differentIndividual","CustomInferredDifferentIndividualAxiomGenerator()" );
        hashMap.put("HasValue", "CustomInferredHasValueAxiomGenerator()"); // Covers owl:hasValue restriction
        hashMap.put("InverseObjectProperties", "CustomInferredInverseObjectPropertiesAxiomGenerator()"); // Covers Inveerse object properties // if property A relates individual x to individual y, then property B relates individual y to individual x
        hashMap.put("AllValuesFrom", "CustomInferredAllValuesFromAxiomGenerator()"); // Cover Owl:allValuesFrom
        hashMap.put("SomeValuesFrom", "CustomInferredSameValueSomeValuesFromAxiomGenerator()"); // Cover Owl:someValuesFrom
        hashMap.put("DomainAndRange", "CustomInferredDomainAndRangeAxiomGenerator()"); // Cover Domain and Range
        hashMap.put("ClassAssertion", "InferredClassAssertionAxiomGenerator()");
        hashMap.put("SubClass", "InferredSubClassAxiomGenerator()"); // Covers Rdfs:SubClass
        hashMap.put("DataPropertyCharacteristic", "InferredDataPropertyCharacteristicAxiomGenerator()");
        hashMap.put("EquivalentDataProperty", "InferredEquivalentDataPropertiesAxiomGenerator()");
        hashMap.put("EquivalentObjectProperty", "InferredEquivalentObjectPropertyAxiomGenerator()");
        hashMap.put("SubObjectProperty", "InferredSubObjectPropertyAxiomGenerator()");
        hashMap.put("SubDataPropertyOfAxiom", "InferredSubDataPropertyAxiomGenerator()");
        hashMap.put("ObjectPropertyCharacteristic", "InferredObjectPropertyCharacteristicAxiomGenerator()"); // FunctionalObjectProperty/InverseFunctionalObjectProperty/SymmetricObjectProperty/AsymmetricObjectProperty/ReflexiveObjectProperty/IrreflexiveObjectProperty/TransitiveObjectProperty
        hashMap.put("SubDataPropertyOfAxiom", "InferredPropertyAssertionGenerator()"); // This Covers both OWLObjectPropertyAssertionAxiom and OWLDataPropertyAssertionAxiom
        hashMap.put("ComplementOf", "CustomInferredComplementOfAxiomGenerator()"); // Cover OwlComplementOf
        hashMap.put("All_OWL", "All"); // Covers Evreything

        return hashMap;

    }

    public static class CustomSameIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLSameIndividualAxiom> {

        @Override
        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSameIndividualAxiom> resultAxiom) {
            for (OWLNamedIndividual x : reasoner.getSameIndividuals(entity).getEntities()) {
                if (!entity.equals(x)) {
                    resultAxiom.add(dataFactory.getOWLSameIndividualAxiom(entity, x));
                }
            }
        }

        @Override
        public String getLabel() {
            return "OWL:SameAs Inferences";
        }
    }

    public class CustomInferredEquivalentClassesAxiomGenerator
            extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

        @Override
        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLEquivalentClassesAxiom> resultAxiom) {
            Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(entity).getEntities();
            if (equivalentClasses.size() > 1) {
                resultAxiom.add(dataFactory.getOWLEquivalentClassesAxiom(equivalentClasses));
            }
        }

        @Override
        public String getLabel() {
            return "owl:equivalentClass Inferences";
        }
    }

    public class CustomInferredDifferentIndividualAxiomGenerator
            extends InferredIndividualAxiomGenerator<OWLDifferentIndividualsAxiom> {

        @Override
        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLDifferentIndividualsAxiom> AxiomResult) {
            Set<OWLNamedIndividual> differentOWLNamedIndividual = reasoner.getDifferentIndividuals(entity).getFlattened();
            if (!differentOWLNamedIndividual.isEmpty()) {
                AxiomResult.add(dataFactory.getOWLDifferentIndividualsAxiom(
                        Stream.concat(Stream.of(entity), differentOWLNamedIndividual.stream()).toArray(OWLIndividual[]::new)));

            }
        }

        @Override
        public String getLabel() {
            return "Different individuals";
        }
    }

    public class CustomInferredIntersectionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

        @Override
        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLEquivalentClassesAxiom> resultAxiom) {
            NodeSet<OWLClass> SuperClasses = reasoner.getSuperClasses(entity, true);
            if (SuperClasses.isEmpty()) {
                return;
            }

            Set<OWLClassExpression> operands = new HashSet<>();
            for (Node<OWLClass> superClassNode : SuperClasses.getNodes()) {
                operands.add(superClassNode.getRepresentativeElement());
            }

            if (operands.size() > 1) {
                OWLObjectIntersectionOf intersectionOf = dataFactory.getOWLObjectIntersectionOf(operands);
                OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(entity, intersectionOf);
                resultAxiom.add(axiom);
            }
        }

        @Override
        public String getLabel() {
            return "OWL:IntersectionOf Inference";
        }
    }

    public class CustomInferredUnionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {
        @Override
        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSubClassOfAxiom> resultAxiom) {
            Set<OWLClass> SuperClasses = reasoner.getSuperClasses(entity, true).getFlattened();
            if (SuperClasses.size() > 1) {
                resultAxiom.add(
                        dataFactory.getOWLSubClassOfAxiom(entity, dataFactory.getOWLObjectUnionOf(SuperClasses)));
            }
        }

        @Override
        public String getLabel() {
            return "OWL:UnionOf Inference";
        }
    }

    public class CustomInferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

        @Override
        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLDisjointClassesAxiom> resultAxiom) {
            Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();
            for (OWLClass classe : allClasses) {
                if (!classe.equals(entity)) {
                    NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(entity);
                    if (disjointClasses.containsEntity(classe)) {

                        Set<OWLClass> equivalentClasses1 = reasoner.getEquivalentClasses(entity).getEntities();
                        Set<OWLClass> equivalentClasses2 = reasoner.getEquivalentClasses(classe).getEntities();
                        for (OWLClass loopEquivalentClass1 : equivalentClasses1) {
                            for (OWLClass loopEquivalentClass2 : equivalentClasses2) {
                                if (!loopEquivalentClass1.equals(loopEquivalentClass2)) {
                                    resultAxiom.add(dataFactory.getOWLDisjointClassesAxiom(loopEquivalentClass1, loopEquivalentClass2));
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String getLabel() {
            return "OWL DisjointClasses";
        }
    }

    public class InferredSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

        @Override
        protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSubClassOfAxiom> result) {
            for (OWLClassExpression superClass : reasoner.getEquivalentClasses(cls).getEntities()) {
                if (superClass.isAnonymous() && superClass instanceof OWLObjectSomeValuesFrom someValuesFrom) {
                    OWLClassExpression filler = someValuesFrom.getFiller();
                    if (!filler.isAnonymous()) {
                        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, someValuesFrom);
                        result.add(axiom);
                    }
                }
            }
        }

        public Set<InferredAxiomGenerator<?>> getInferredAxiomGenerators() {
            return Collections.emptySet();
        }

        @Override
        public String getLabel() {
            return "Some values from";
        }
    }

    public class CustomInferredHasValueAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

        @Override
        protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSubClassOfAxiom> resultAxiom) {
            for (OWLClassExpression equivalentClass : reasoner.getEquivalentClasses(cls).getEntities()) {
                if (equivalentClass.isAnonymous() && equivalentClass instanceof OWLObjectHasValue owlHasValue) {
                    OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, owlHasValue);
                    resultAxiom.add(axiom);
                }
            }
        }

        public Set<InferredAxiomGenerator<?>> getInferredAxiomGenerators() {
            return Collections.emptySet();
        }

        @Override
        public String getLabel() {
            return "Owl:hasValue Inferences";
        }
    }

    public class CustomInferredInverseObjectPropertiesAxiomGenerator
            implements InferredAxiomGenerator<OWLInverseObjectPropertiesAxiom> {

        @Override
        public Set<OWLInverseObjectPropertiesAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
            Set<OWLInverseObjectPropertiesAxiom> resultSet = new HashSet<>();
            for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
                addAxioms(objectProperty, reasoner, dataFactory, resultSet);
            }
            return resultSet;
        }

        protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLInverseObjectPropertiesAxiom> resultAxiom) {
            for (OWLObjectPropertyExpression inverseAxiom : reasoner.getInverseObjectProperties(entity).getEntities()) {
                if (!inverseAxiom.equals(entity)) {
                    resultAxiom.add(dataFactory.getOWLInverseObjectPropertiesAxiom(entity, inverseAxiom));
                }
            }
        }

        @Override
        public String getLabel() {
            return "owl:InverseFunctionalProperty";
        }
    }

    public class CustomInferredAllValuesFromAxiomGenerator implements InferredAxiomGenerator<OWLSubClassOfAxiom> {

        @Override
        public Set<OWLSubClassOfAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
            Set<OWLSubClassOfAxiom> resultSet = new HashSet<>();
            for (OWLClass owlClass : reasoner.getRootOntology().getClassesInSignature()) {
                addAxioms(owlClass, reasoner, dataFactory, resultSet);
            }
            return resultSet;
        }

        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSubClassOfAxiom> resultAxiom) {
            for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
                NodeSet<OWLClass> possibleRanges = reasoner.getObjectPropertyRanges(objectProperty, true);
                if (!possibleRanges.isEmpty()) {
                    OWLClassExpression owlAllValuesFrom = dataFactory.getOWLObjectAllValuesFrom(objectProperty,
                            possibleRanges.getFlattened().iterator().next());
                    OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entity, owlAllValuesFrom);
                    resultAxiom.add(axiom);
                }
            }
        }

        @Override
        public String getLabel() {
            return "owl:allValuesFrom ";
        }
    }

    public class CustomInferredSameValueSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

        @Override
        protected void addAxioms(OWLClass entty, OWLReasoner reasoner, OWLDataFactory dataFactory,
                                 Set<OWLSubClassOfAxiom> resultAxioms) {
            for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
                NodeSet<OWLClass> possibleRanges = reasoner.getObjectPropertyRanges(objectProperty, true);
                if (!possibleRanges.isEmpty()) {
                    OWLClassExpression owlSomeValuesFromRslt = dataFactory.getOWLObjectSomeValuesFrom(objectProperty,
                            possibleRanges.getFlattened().iterator().next());
                    OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entty, owlSomeValuesFromRslt);
                    resultAxioms.add(axiom);
                }
            }
        }

        @Override
        public String getLabel() {
            return "owl:someValueFrom";
        }
    }

    public class CustomInferredDomainAndRangeAxiomGenerator implements InferredAxiomGenerator<OWLObjectPropertyAxiom> {

        @Override
        public Set<OWLObjectPropertyAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
            Set<OWLObjectPropertyAxiom> resultSet = new HashSet<>();
            // Two loops to get inference from domain and range
            for (OWLObjectProperty property : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
                for (OWLClass domainInf : reasoner.getObjectPropertyDomains(property, true).getFlattened()) {
                    resultSet.add(dataFactory.getOWLObjectPropertyDomainAxiom(property, domainInf));
                }
                for (OWLClass rangeInf : reasoner.getObjectPropertyRanges(property, true).getFlattened()) {
                    resultSet.add(dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeInf));
                }
            }

            return resultSet;
        }

        @Override
        public String getLabel() {
            return "Inference of Domain And Range";
        }
    }

    public class CustomInferredComplementOfAxiomGenerator implements InferredAxiomGenerator<OWLClassAxiom> {

        @Override
        public Set<OWLClassAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
            Set<OWLClassAxiom> resultSet = new HashSet<>();

            Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();

            for (OWLClass cls : allClasses) {
                if (!reasoner.isSatisfiable(cls) && !cls.isOWLNothing()) {
                    NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(cls);
                    disjointClasses.getFlattened().forEach(disjointClass -> {
                        OWLObjectComplementOf ObjectComplementOf = dataFactory.getOWLObjectComplementOf(disjointClass);
                        OWLEquivalentClassesAxiom axiomEQ = dataFactory.getOWLEquivalentClassesAxiom(cls, ObjectComplementOf);
                        resultSet.add(axiomEQ);
                    });
                }
            }

            return resultSet;
        }

        @Override
        public String getLabel() {
            return "complementOf";
        }
    }

    public class CustomInferredOntologyGenerator extends InferredOntologyGenerator {

        public CustomInferredOntologyGenerator(OWLReasoner reasoner) {
            super(reasoner);
        }
    }

}
