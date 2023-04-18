package com.souslesens.Jowl;

import java.io.File;
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
import com.souslesens.Jowl.model.myData;

@RestController
public class ReasonerController {

	private static final String template = "Hello, %s!";
	private static final String url  = "https://protege.stanford.edu/ontologies/camera.owl";
	
    @GetMapping(value = "/")
    public ResponseEntity<String> pong() 
    {
        
        return new ResponseEntity<String>("Réponse du serveur: "+HttpStatus.OK.name(), HttpStatus.OK);
    }
	
//	@GetMapping("/reasoner")
//	public Reasoner reasoner(@RequestParam(value = "name", defaultValue = "World") String name) {
//		return new Reasoner(url,String.format(template, name));
//	}
    @RequestMapping("/reasoner")
    public ResponseEntity<?> reasoner(@RequestParam(value = "filePath", defaultValue = "https://protege.stanford.edu/ontologies/camera.owl") String filePath, @RequestParam(value = "operation", defaultValue = "consistency") String operation) throws OWLOntologyCreationException, JsonProcessingException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ontology;
        if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
        	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        boolean consistency = reasoner.isConsistent();
	        Set<OWLAxiom> axiom = ontology.getAxioms();
	        
	        myData myData = new myData(consistency, axiom.toArray()[0].toString());
	        ObjectMapper objectMapper = new ObjectMapper();
	        String json = objectMapper.writeValueAsString(myData);
	        
	        return ResponseEntity.status(HttpStatus.ACCEPTED).body(json  );
	        
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
	        boolean consistency = reasoner.isConsistent();
	        if (!consistency) {
	        	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD");
	        }
		    return ResponseEntity.status(HttpStatus.ACCEPTED).body("gg");
		}

    }
}