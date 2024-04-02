package com.souslesens.Jowl.services;

import org.json.JSONObject;
import org.openjena.atlas.json.JSON;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class HermitReasonerServiceImpl implements HermitReasonerService {

    @Override
    public String getUnsatisfaisableClasses(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (filePath.isEmpty() == false && Url == null) {
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
    public String getConsistency(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (filePath.isEmpty() == false && Url == null) {
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
    public String getInferences(String filePath, String Url) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
            } else if (filePath.isEmpty() == false && Url == null) {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return e.getMessage();
        }        Configuration config = new Configuration();
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
}
