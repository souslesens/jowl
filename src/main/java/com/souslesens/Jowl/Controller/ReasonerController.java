package com.souslesens.Jowl.Controller;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souslesens.Jowl.model.reasonerOutput;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

@RestController
public class ReasonerController {

	private static final String template = "Hello, %s!";
	private static final String url  = "https://protege.stanford.edu/ontologies/pizza.owl";
	
    @GetMapping(value = "/")
    public ResponseEntity<String> pong() 
    {
        
        return new ResponseEntity<String>("Réponse du serveur: "+HttpStatus.OK.name(), HttpStatus.OK);
    }
	
    @RequestMapping("/reasoner/unsatisfaisable")
    public ResponseEntity<?> reasonerUn(@RequestParam(value = "filePath", defaultValue = "https://protege.stanford.edu/ontologies/pizza/pizza.owl") String filePath, @RequestParam(value = "operation", defaultValue = "consistency") String operation) throws OWLOntologyCreationException, JsonProcessingException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ontology;
        if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
        	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
	        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
	        System.out.println(unsatisfiableClasses.getSize());
	        OWLClass[] unsatisfiable =  new OWLClass[unsatisfiableClasses.getSize()];
	        int i = 0;
	        if (unsatisfiableClasses.getSize() > 0) {
                for (OWLClass cls : unsatisfiableClasses) {
                	System.out.println(cls.getClass());
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
            System.out.println(Arrays.toString(unsatisfiableArray));
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        String json = objectMapper.writeValueAsString(Arrays.toString(iriStrings));
	        if (json.isEmpty()) {
	        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("There's no unsatisaible classes");
	        }
	        return ResponseEntity.status(HttpStatus.ACCEPTED).body(json);
	        
//	        if (!consistency) {
//	        	System.out.println("aaaaaaa NO NO NO");
//	        	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD");
//	        }
//        	
//		    return ResponseEntity.status(HttpStatus.ACCEPTED).body("gg");
		} else {
			OWLOntology	ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
	        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
	        System.out.println(unsatisfiableClasses.getSize());
	        OWLClass[] unsatisfiable =  new OWLClass[unsatisfiableClasses.getSize()];
	        int i = 0;
	        if (unsatisfiableClasses.getSize() > 0) {
                for (OWLClass cls : unsatisfiableClasses) {
                	System.out.println(cls.getClass());
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
            System.out.println(Arrays.toString(unsatisfiableArray));
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        String json = objectMapper.writeValueAsString(Arrays.toString(iriStrings));
	        if (json.isEmpty()) {
	        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("There's no unsatisfsable classes");
	        }
		    return ResponseEntity.status(HttpStatus.ACCEPTED).body(json);
		}

    }

@RequestMapping("/reasoner/consistency")
public ResponseEntity<?> reasonerCon(@RequestParam(value = "filePath", defaultValue = "https://protege.stanford.edu/ontologies/pizza/pizza.owl") String filePath, @RequestParam(value = "operation", defaultValue = "consistency") String operation) throws OWLOntologyCreationException, JsonProcessingException {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//    OWLOntology ontology;
    if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
    	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
        System.out.println(unsatisfiableClasses.getSize());
        OWLClass[] unsatisfiable =  new OWLClass[unsatisfiableClasses.getSize()];
        int i = 0;
        if (unsatisfiableClasses.getSize() > 0) {
            for (OWLClass cls : unsatisfiableClasses) {
            	System.out.println(cls.getClass());
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
        System.out.println(Arrays.toString(unsatisfiableArray));
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Arrays.toString(iriStrings));
        if (json.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("There's no unsatisaible classes");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(json);
        
	} else {
		OWLOntology	ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
        System.out.println(unsatisfiableClasses.getSize());
        OWLClass[] unsatisfiable =  new OWLClass[unsatisfiableClasses.getSize()];
        int i = 0;
        if (unsatisfiableClasses.getSize() > 0) {
            for (OWLClass cls : unsatisfiableClasses) {
            	System.out.println(cls.getClass());
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
        System.out.println(Arrays.toString(unsatisfiableArray));
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Arrays.toString(iriStrings));
        if (json.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("There's no unsatisfsable classes");
        }
	    return ResponseEntity.status(HttpStatus.ACCEPTED).body(json);
	}

}
}