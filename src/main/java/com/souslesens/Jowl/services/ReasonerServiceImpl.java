package com.souslesens.Jowl.services;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerInference;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;
@Service

public class ReasonerServiceImpl implements ReasonerService{
	
	
	
	 @Override
		public String getUnsatisfaisableClasses(String filePath, String Url) throws Exception {
	        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
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
	        return jsonString;
	    }
	 // TEST

	 @Override
	 public String postInferencesContent(String ontologyContentDecoded64) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     String filePath = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    filePath = "output.owl";
	    		    
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	     	if(filePath.isEmpty() == false) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
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
	        }
	        // Load the ontology from the file
	        ontology = manager.loadOntologyFromOntologyDocument(resource.getFile());
	        
	        StringBuilder sb = new StringBuilder();
	        // Add the inferred axioms to the list
	        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
	        	sb.append(axiom.toString());
	            
	        }
	        String axiomsString = sb.toString();
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("inference", axiomsString);
	         String jsonString = jsonObject.toString();
	         return jsonString;
	    }
	 
//	        System.out.println("Ontology ComputeInference Completed");
//	        Set<OWLAxiom> axioms = ontology.getAxioms();
//	        List<reasonerExtractTriples> triplesList = new ArrayList<>(); 
//	        // Iterate through axioms
//	        for (OWLAxiom axiom : axioms) {
//	            String subject = null;
//	            String predicate = null;
//	            String object = null;
//	            
//	            // Extract subject and object
//	            if (axiom instanceof OWLSubClassOfAxiom) {
//	                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//	                subject = subClassAxiom.getSubClass().toString();
//	                object = subClassAxiom.getSuperClass().toString();
//	                predicate = "subClassOf";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLEquivalentClassesAxiom) {
//	                OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
//	                Set<OWLClassExpression> classExpressions = equivClassesAxiom.getClassExpressions();
//	                for (OWLClassExpression classExpression : classExpressions) {
//	                    if (!classExpression.isAnonymous()) {
//	                        if (subject == null) {
//	                            subject = classExpression.asOWLClass().toStringID();
//	                            predicate = "equivalentTo";
//	                        } else {
//	                            object = classExpression.asOWLClass().toStringID();
//	                        }
//	                        if (subject != null && predicate != null && object !=null) {
//	                            triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                            }
//	    	                
//	                    }
//	                }
//	            } else if (axiom instanceof OWLClassAssertionAxiom) {
//	                OWLClassAssertionAxiom classAssertionAxiom = (OWLClassAssertionAxiom) axiom;
//	                subject = classAssertionAxiom.getIndividual().toStringID();
//	                object = classAssertionAxiom.getClassExpression().toString();
//	                predicate = "type";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
//	                OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
//	                subject = objectPropertyAssertionAxiom.getSubject().toStringID();
//	                object = objectPropertyAssertionAxiom.getObject().toStringID();
//	                predicate = objectPropertyAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
//	                OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
//	                subject = dataPropertyAssertionAxiom.getSubject().toStringID();
//	                object = dataPropertyAssertionAxiom.getObject().toString();
//	                predicate = dataPropertyAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLAnnotationAssertionAxiom) {
//	                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
//	                subject = annotationAssertionAxiom.getSubject().toString();
//	                object = annotationAssertionAxiom.getValue().toString();
//	                predicate = annotationAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
//	                OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
//	                subject = objectPropertyDomainAxiom.getProperty().toString();
//	                object = objectPropertyDomainAxiom.getDomain().toString();
//	                predicate = "domain";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
//	                OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
//	                subject = objectPropertyRangeAxiom.getProperty().toString();
//	                object = objectPropertyRangeAxiom.getRange().toString();
//	                predicate = "range";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
//	                OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;
//	                subject = functionalObjectPropertyAxiom.getProperty().toString();
//	                predicate = "functionalProperty";
//	                
//	                if (subject != null && predicate != null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLDeclarationAxiom) {
//	                OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
//	                if (declarationAxiom.getEntity() instanceof OWLClass) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "classDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLObjectProperty) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "objectPropertyDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLDataProperty) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "dataPropertyDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLNamedIndividual) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "namedIndividualDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                }else if (declarationAxiom.getEntity() instanceof OWLDatatype) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "datatypeDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                }
//	            }
//
//	            
//	        }
//	        return triplesList;
//	         
//	 	}
//			return null;
//	 }
	 @Override
	 public String postUnsatisfaisableClassesContent(String ontologyContentDecoded64) throws Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     String filePath = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    filePath = "output.owl";
	    		    
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	     	if(filePath.isEmpty() == false) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
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
	        return jsonString;
	    }
	 
	 @Override
	 public String postConsistencyContent(String ontologyContentDecoded64) throws OWLOntologyCreationException, JsonProcessingException,Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 System.out.println("From HERe \n"+ontologyContentDecoded64);
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    String filePath = "output.owl";
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	       
	         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	         reasonerConsistency myData = new reasonerConsistency();
	         boolean consistency = reasoner.isConsistent();
	         System.out.println(consistency);
	         myData.setConsistency(consistency);
	         JSONObject jsonObject = new JSONObject();
	         jsonObject.put("consistency", myData.getConsistency());
	         String jsonString = jsonObject.toString();
	         return jsonString;
	         
	         
	 	}
	 
	 
	 
	 @Override
	 public String postInferences(String filePath, String url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;

	        if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
	        	
	        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
	        } else if(filePath.isEmpty() == false && url == null) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
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
	        }
	        // Load the ontology from the file
	        ontology = manager.loadOntologyFromOntologyDocument(resource.getFile());
	        
	        StringBuilder sb = new StringBuilder();
	        // Add the inferred axioms to the list
	        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
	        	sb.append(axiom.toString());
	            
	        }
	        String axiomsString = sb.toString();
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("inference", axiomsString);
	         String jsonString = jsonObject.toString();
	         return jsonString;
	    }
	 
//	        System.out.println("Ontology ComputeInference Completed");
//	        Set<OWLAxiom> axioms = ontology.getAxioms();
//	        List<reasonerExtractTriples> triplesList = new ArrayList<>(); 
//	        // Iterate through axioms
//	        for (OWLAxiom axiom : axioms) {
//	            String subject = null;
//	            String predicate = null;
//	            String object = null;
//	            
//	            // Extract subject and object
//	            if (axiom instanceof OWLSubClassOfAxiom) {
//	                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//	                subject = subClassAxiom.getSubClass().toString();
//	                object = subClassAxiom.getSuperClass().toString();
//	                predicate = "subClassOf";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLEquivalentClassesAxiom) {
//	                OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
//	                Set<OWLClassExpression> classExpressions = equivClassesAxiom.getClassExpressions();
//	                for (OWLClassExpression classExpression : classExpressions) {
//	                    if (!classExpression.isAnonymous()) {
//	                        if (subject == null) {
//	                            subject = classExpression.asOWLClass().toStringID();
//	                            predicate = "equivalentTo";
//	                        } else {
//	                            object = classExpression.asOWLClass().toStringID();
//	                        }
//	                        if (subject != null && predicate != null && object !=null) {
//	                            triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                            }
//	    	                
//	                    }
//	                }
//	            } else if (axiom instanceof OWLClassAssertionAxiom) {
//	                OWLClassAssertionAxiom classAssertionAxiom = (OWLClassAssertionAxiom) axiom;
//	                subject = classAssertionAxiom.getIndividual().toStringID();
//	                object = classAssertionAxiom.getClassExpression().toString();
//	                predicate = "type";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
//	                OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
//	                subject = objectPropertyAssertionAxiom.getSubject().toStringID();
//	                object = objectPropertyAssertionAxiom.getObject().toStringID();
//	                predicate = objectPropertyAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
//	                OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
//	                subject = dataPropertyAssertionAxiom.getSubject().toStringID();
//	                object = dataPropertyAssertionAxiom.getObject().toString();
//	                predicate = dataPropertyAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLAnnotationAssertionAxiom) {
//	                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
//	                subject = annotationAssertionAxiom.getSubject().toString();
//	                object = annotationAssertionAxiom.getValue().toString();
//	                predicate = annotationAssertionAxiom.getProperty().toString();
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
//	                OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
//	                subject = objectPropertyDomainAxiom.getProperty().toString();
//	                object = objectPropertyDomainAxiom.getDomain().toString();
//	                predicate = "domain";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
//	                OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
//	                subject = objectPropertyRangeAxiom.getProperty().toString();
//	                object = objectPropertyRangeAxiom.getRange().toString();
//	                predicate = "range";
//	                if (subject != null && predicate != null && object !=null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
//	                OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;
//	                subject = functionalObjectPropertyAxiom.getProperty().toString();
//	                predicate = "functionalProperty";
//	                
//	                if (subject != null && predicate != null) {
//	                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                }
//	            } else if (axiom instanceof OWLDeclarationAxiom) {
//	                OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
//	                if (declarationAxiom.getEntity() instanceof OWLClass) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "classDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLObjectProperty) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "objectPropertyDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLDataProperty) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "dataPropertyDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                } else if (declarationAxiom.getEntity() instanceof OWLNamedIndividual) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "namedIndividualDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                }else if (declarationAxiom.getEntity() instanceof OWLDatatype) {
//	                    subject = declarationAxiom.getEntity().toStringID();
//	                    predicate = "datatypeDeclaration";
//	                    if (subject != null && predicate != null) {
//	                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//	                    }
//	                }
//	            }
//
//	            
//	        }
//	        return triplesList;
//	         
//	 	}
//			return null;
//	 }
	 @Override
	 public String postUnsatisfaisableClasses(String filePath, String Url) throws Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
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
	        return jsonString;
	    }
	 
	 @Override
	 public String postConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException,Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
	        	
	        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
	        } else if(filePath.isEmpty() == false && Url == null) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
	         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	         reasonerConsistency myData = new reasonerConsistency();
	         boolean consistency = reasoner.isConsistent();
	         System.out.println(consistency);
	         myData.setConsistency(consistency);
	         JSONObject jsonObject = new JSONObject();
	         jsonObject.put("consistency", myData.getConsistency());
	         String jsonString = jsonObject.toString();
	         return jsonString;
	         
	         
	 	}
	 
	 
	 // END
		 @Override
		 public String getConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException,Exception {
		     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }
		         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		         reasonerConsistency myData = new reasonerConsistency();
		         boolean consistency = reasoner.isConsistent();
		         System.out.println(consistency);
		         myData.setConsistency(consistency);
		         JSONObject jsonObject = new JSONObject();
		         jsonObject.put("consistency", myData.getConsistency());
		         String jsonString = jsonObject.toString();
		         return jsonString;
		         
		         
		 	}
		 @Override
		 public String getInferences(String filePath, String url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		     OWLOntology ontology = null ;
		        if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
		        } else if(filePath.isEmpty() == false && url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }
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
		        } 
		        // Load the ontology from the file
		        ontology = manager.loadOntologyFromOntologyDocument(resource.getFile());
		        StringBuilder sb = new StringBuilder();
		        // Add the inferred axioms to the list
		        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
		        	sb.append(axiom.toString());
		            
		        }
		        String axiomsString = sb.toString();
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("inference", axiomsString);
		         String jsonString = jsonObject.toString();
		         return jsonString;
		    }

}
//		        System.out.println("Ontology ComputeInference Completed");
//		        Set<OWLAxiom> axioms = ontology.getAxioms();
//		        List<reasonerExtractTriples> triplesList = new ArrayList<>(); 
//		        // Iterate through axioms
//		        for (OWLAxiom axiom : axioms) {
//		            String subject = null;
//		            String predicate = null;
//		            String object = null;
//		            
//		            // Extract subject and object
//		            if (axiom instanceof OWLSubClassOfAxiom) {
//		                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//		                subject = subClassAxiom.getSubClass().toString();
//		                object = subClassAxiom.getSuperClass().toString();
//		                predicate = "subClassOf";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLEquivalentClassesAxiom) {
//		                OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
//		                Set<OWLClassExpression> classExpressions = equivClassesAxiom.getClassExpressions();
//		                for (OWLClassExpression classExpression : classExpressions) {
//		                    if (!classExpression.isAnonymous()) {
//		                        if (subject == null) {
//		                            subject = classExpression.asOWLClass().toStringID();
//		                            predicate = "equivalentTo";
//		                        } else {
//		                            object = classExpression.asOWLClass().toStringID();
//		                        }
//		                        if (subject != null && predicate != null && object !=null) {
//		                            triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                            }
//		    	                
//		                    }
//		                }
//		            } else if (axiom instanceof OWLClassAssertionAxiom) {
//		                OWLClassAssertionAxiom classAssertionAxiom = (OWLClassAssertionAxiom) axiom;
//		                subject = classAssertionAxiom.getIndividual().toStringID();
//		                object = classAssertionAxiom.getClassExpression().toString();
//		                predicate = "type";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
//		                OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
//		                subject = objectPropertyAssertionAxiom.getSubject().toStringID();
//		                object = objectPropertyAssertionAxiom.getObject().toStringID();
//		                predicate = objectPropertyAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
//		                OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
//		                subject = dataPropertyAssertionAxiom.getSubject().toStringID();
//		                object = dataPropertyAssertionAxiom.getObject().toString();
//		                predicate = dataPropertyAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLAnnotationAssertionAxiom) {
//		                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
//		                subject = annotationAssertionAxiom.getSubject().toString();
//		                object = annotationAssertionAxiom.getValue().toString();
//		                predicate = annotationAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
//		                OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
//		                subject = objectPropertyDomainAxiom.getProperty().toString();
//		                object = objectPropertyDomainAxiom.getDomain().toString();
//		                predicate = "domain";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
//		                OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
//		                subject = objectPropertyRangeAxiom.getProperty().toString();
//		                object = objectPropertyRangeAxiom.getRange().toString();
//		                predicate = "range";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
//		                OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;
//		                subject = functionalObjectPropertyAxiom.getProperty().toString();
//		                predicate = "functionalProperty";
//		                
//		                if (subject != null && predicate != null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLDeclarationAxiom) {
//		                OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
//		                if (declarationAxiom.getEntity() instanceof OWLClass) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "classDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLObjectProperty) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "objectPropertyDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLDataProperty) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "dataPropertyDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLNamedIndividual) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "namedIndividualDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                }else if (declarationAxiom.getEntity() instanceof OWLDatatype) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "datatypeDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                }
//		            }
//
//		            
//		        }
//		        return triplesList;
//		         
//		 	}
//				return null;
//		 }





