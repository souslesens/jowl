package com.souslesens.Jowl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicLong;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

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
    @PostMapping("/reasoner")
    public ResponseEntity<?> reasoner(@RequestParam String filePath, @RequestParam String operation) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
                ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
            } else {
                ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
            }
        } catch (OWLOntologyCreationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to load ontology");
        }

        OWLReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
        switch (operation) {
            case "satisfiability":
                Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
                return ResponseEntity.ok(unsatisfiableClasses);
            case "consistency":
                boolean consistency = reasoner.isConsistent();
                return ResponseEntity.ok(consistency);
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid operation");
        }
    }
}