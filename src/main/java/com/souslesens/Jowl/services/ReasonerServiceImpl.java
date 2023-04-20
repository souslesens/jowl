package com.souslesens.Jowl.services;
import java.io.File;
import java.util.Arrays;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;
@Service
public class ReasonerServiceImpl implements ReasonerService{
	 @Override
	public String getUnsatisfaisableClasses(String filePath, String operation) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        OWLOntology ontology;
        if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
        } else {
            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
        }

        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();

        OWLClass[] unsatisfiable = new OWLClass[unsatisfiableClasses.getSize()];
        int i = 0;
        if (unsatisfiableClasses.getSize() > 0) {
            for (OWLClass cls : unsatisfiableClasses) {
                unsatisfiable[i] = cls;
                i++;
            }
            myData.setUnsatisfaisable(unsatisfiable);
        }

        OWLClass[] unsatisfiableArray = myData.getUnsatisfaisable();
        String[] iriStrings = new String[unsatisfiableArray.length];
        for (int j = 0; j < unsatisfiableArray.length; j++) {
            iriStrings[j] = unsatisfiableArray[j].toStringID();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unsatisfiable", iriStrings);

        String jsonString = jsonObject.toString();
        System.out.println(jsonString); // just for debugging
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(Arrays.toString(iriStrings));

//        if (json.isEmpty()) {
//            throw new Exception("There's no unsatisaible classes");
//        }

        return jsonString;
    }
	 @Override
	 public String getConsistency(String filePath, String operation) throws OWLOntologyCreationException, JsonProcessingException,Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology;
	        if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
	            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
	        } else {
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        }
	         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	         reasonerConsistency myData = new reasonerConsistency();
	         boolean consistency = reasoner.isConsistent();
	         System.out.println(consistency); // for debuggin purposes
	         myData.setConsistency(consistency);
//	         ObjectMapper objectMapper = new ObjectMapper();
//	         String json = objectMapper.writeValueAsString(myData.getConsistency());
	         JSONObject jsonObject = new JSONObject();
	         jsonObject.put("consistency", myData.getConsistency());

	         String jsonString = jsonObject.toString();
	         System.out.println(jsonString);
	         return jsonString;
	         
	         
	 	}

	 }

