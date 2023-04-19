package com.souslesens.Jowl.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;

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
    public ResponseEntity<?> reasoner(@RequestParam(value = "filePath", defaultValue = "https://protege.stanford.edu/ontologies/pizza/pizza.owl") String filePath, @RequestParam(value = "operation", defaultValue = "consistency") String operation) throws OWLOntologyCreationException, JsonProcessingException {
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
        reasonerConsistency myData = new reasonerConsistency();
        boolean consistency = reasoner.isConsistent();
        System.out.println(consistency);
        myData.setConsistency(consistency);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(myData.getConsistency());
        if (consistency) {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Consistency of the ontology : " + json);
        }else {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Consistency of the ontology : " + json);
        }
        
        
	} else {
		OWLOntology	ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        reasonerConsistency myData = new reasonerConsistency();
        boolean consistency = reasoner.isConsistent();
        System.out.println(consistency);
        myData.setConsistency(consistency);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(myData.getConsistency());
        if (consistency) {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Consistency of the ontology : " + json);
        }else {
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Consistency of the ontology : " + json);
        }
	}

}
@RequestMapping("/reasoner/inference")
public ResponseEntity<?> reasonerInf(@RequestParam(value = "filePath", defaultValue = "https://protege.stanford.edu/ontologies/pizza/pizza.owl") String filePath, @RequestParam(value = "operation", defaultValue = "precomputeinference") String operation) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//    OWLOntology ontology;
    if (filePath.startsWith("http") || filePath.startsWith("ftp")) {
    	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        String fileName = "inferred-ontology.owl";
        reasoner.precomputeInferences(InferenceType.values());
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
        OWLOntology inferredOntology = manager.createOntology();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        iog.fillOntology(dataFactory, inferredOntology);

        Resource resource = new FileSystemResource(fileName);
        if (resource instanceof WritableResource) {
            try (OutputStream outputStream =((WritableResource) resource).getOutputStream()) {
            manager.saveOntology(inferredOntology, IRI.create(resource.getURI()));
            System.out.println("New file created: " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        // Load the ontology from the file
        ontology = manager.loadOntologyFromOntologyDocument(resource.getFile());

        // Print the inferred axioms
        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
            System.out.println("THE INFERRED AXIOMS"+axiom);
        }

        System.out.println("Ontology ComputeInference Completed");
        reasonerExtractTriples myData = new reasonerExtractTriples();
        Set<OWLAxiom> axioms = ontology.getAxioms();

        // Iterate through axioms
        for (OWLAxiom axiom : axioms) {
            String subject = null;
            String predicate = null;
            String object = null;

            // Extract subject and object
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                subject = subClassAxiom.getSubClass().toString();
                object = subClassAxiom.getSuperClass().toString();
                predicate = "subClassOf";
            } else if (axiom instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
                Set<OWLClassExpression> classExpressions = equivClassesAxiom.getClassExpressions();
                for (OWLClassExpression classExpression : classExpressions) {
                    if (!classExpression.isAnonymous()) {
                        if (subject == null) {
                            subject = classExpression.asOWLClass().toStringID();
                            predicate = "equivalentTo";
                        } else {
                            object = classExpression.asOWLClass().toStringID();
                        }
                    }
                }
            } else if (axiom instanceof OWLClassAssertionAxiom) {
                OWLClassAssertionAxiom classAssertionAxiom = (OWLClassAssertionAxiom) axiom;
                subject = classAssertionAxiom.getIndividual().toStringID();
                object = classAssertionAxiom.getClassExpression().toString();
                predicate = "type";
            } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
                OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
                subject = objectPropertyAssertionAxiom.getSubject().toStringID();
                object = objectPropertyAssertionAxiom.getObject().toStringID();
                predicate = objectPropertyAssertionAxiom.getProperty().toString();
            } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
                OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
                subject = dataPropertyAssertionAxiom.getSubject().toStringID();
                object = dataPropertyAssertionAxiom.getObject().toString();
                predicate = dataPropertyAssertionAxiom.getProperty().toString();
            } else if (axiom instanceof OWLAnnotationAssertionAxiom) {
                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
                subject = annotationAssertionAxiom.getSubject().toString();
                object = annotationAssertionAxiom.getValue().toString();
                predicate = annotationAssertionAxiom.getProperty().toString();
            } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
                OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
                subject = objectPropertyDomainAxiom.getProperty().toString();
                object = objectPropertyDomainAxiom.getDomain().toString();
                predicate = "domain";
            } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
                OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
                subject = objectPropertyRangeAxiom.getProperty().toString();
                object = objectPropertyRangeAxiom.getRange().toString();
                predicate = "range";
            } else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
                OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;
                subject = functionalObjectPropertyAxiom.getProperty().toString();
                predicate = "functionalProperty";
            } else if (axiom instanceof OWLDeclarationAxiom) {
                OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
                if (declarationAxiom.getEntity() instanceof OWLClass) {
                    subject = declarationAxiom.getEntity().toStringID();
                    predicate = "classDeclaration";
                } else if (declarationAxiom.getEntity() instanceof OWLObjectProperty) {
                    subject = declarationAxiom.getEntity().toStringID();
                    predicate = "objectPropertyDeclaration";
                } else if (declarationAxiom.getEntity() instanceof OWLDataProperty) {
                    subject = declarationAxiom.getEntity().toStringID();
                    predicate = "dataPropertyDeclaration";
                } else if (declarationAxiom.getEntity() instanceof OWLNamedIndividual) {
                    subject = declarationAxiom.getEntity().toStringID();
                    predicate = "namedIndividualDeclaration";
                } else if (declarationAxiom.getEntity() instanceof OWLDatatype) {
                    subject = declarationAxiom.getEntity().toStringID();
                    predicate = "datatypeDeclaration";
                }
            }

            // Print the triple
            if (subject != null && predicate != null && object != null) {
            	
                System.out.println("aaaaaaaaaaa"+subject + "---->" + predicate + "---->" + object + '\n');
            }
            if ( subject != null && predicate != null) {
            	System.out.println("ONLY TWO"+ subject+"==>"+predicate);
            }
        }
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(myData);
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Infered Triples : " );
        }

        
        
    }else {
		OWLOntology	ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(myData.getConsistency());
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body("Consistency of the ontology : ");
        }
	return null;
	}

}
