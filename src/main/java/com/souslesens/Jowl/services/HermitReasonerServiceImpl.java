package com.souslesens.Jowl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class HermitReasonerServiceImpl implements HermitReasonerService {

    @Override
    public String getUnsatisfaisableClasses(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (!filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
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
    public String getConsistency(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (!filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
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
    public String getInferences(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && !Url.isEmpty() && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (!filePath.isEmpty() && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        Configuration config = new Configuration();
        Reasoner hermit = new Reasoner(config, ontology);
        hermit.precomputeInferences(InferenceType.values());

        InferredOntologyGenerator generator = new InferredOntologyGenerator(hermit);

        OWLOntology inferredOntology = manager.createOntology();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        generator.fillOntology(dataFactory, inferredOntology);

        StringBuilder sb = new StringBuilder();
        // Add the inferred axioms to the list
        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
            sb.append(axiom.toString());

        }
        String axiomsString = sb.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inference", axiomsString);

        return jsonObject.toString();

    }

    @Override
    public String getInferences(String ontologyContentDecoded64)
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

        OWLOntology inferredOntology = manager.createOntology();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        generator.fillOntology(dataFactory, inferredOntology);

        StringBuilder sb = new StringBuilder();
        // Add the inferred axioms to the list
        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
            sb.append(axiom.toString());

        }
        String axiomsString = sb.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inference", axiomsString);

        return jsonObject.toString();

    }



}
