package com.souslesens.Jowl.services;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerConsistency;
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
		     	if(filePath.isEmpty() == false) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        }
	    	
	     }
	     	else {
	        	return null;
	        }
	     	 try {
	             File file = new File(filePath);
	             FileWriter writer = new FileWriter(file);
	             writer.write("");
	             writer.close();
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        reasoner.precomputeInferences(InferenceType.values());
	        OWLOntology inferredOntology = manager.createOntology();
	        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
	        iog.addGenerator(new InferredEquivalentClassesAxiomGenerator());
	        iog.addGenerator(new SameIndividualAxiomGenerator()); // Add custom generator for same individual axioms
	        iog.addGenerator(new InferredDifferentIndividualAxiomGenerator()); // Add custom generator for different individual axioms
	        iog.addGenerator(new InferredIntersectionOfAxiomGenerator());
	        iog.addGenerator(new InferredUnionOfAxiomGenerator());
	        iog.addGenerator(new InferredDisjointClassesAxiomGenerator());
	        iog.addGenerator(new InferredHasValueAxiomGenerator());
	        iog.addGenerator(new InferredInverseObjectPropertiesAxiomGenerator() );
	        iog.addGenerator(new InferredAllValuesFromAxiomGenerator());
	        iog.addGenerator(new InferredSameValueSomeValuesFromAxiomGenerator());
	        iog.addGenerator(new InferredDomainAndRangeAxiomGenerator());
	        // Can be deleted
	        iog.addGenerator(new InferredClassAssertionAxiomGenerator());
	        iog.addGenerator(new InferredEquivalentDataPropertiesAxiomGenerator());
	        iog.addGenerator(new InferredObjectPropertyCharacteristicAxiomGenerator());
	        iog.addGenerator(new InferredEquivalentObjectPropertyAxiomGenerator());
	        iog.addGenerator(new InferredPropertyAssertionGenerator());
	        // Can be deleted
	        OWLDataFactory dataFactory = manager.getOWLDataFactory();
	        iog.fillOntology(dataFactory, inferredOntology);
	        System.out.println("Infered Ontologie \n"+inferredOntology);
	     // Extract the specified axioms and expressions
	        JSONObject jsonObject = new JSONObject();
	        for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
	            Set<? extends OWLAxiom> axioms = inferredOntology.getAxioms(axiomType);
	            System.out.println(convertAxiomSetToJSONArray(axioms));
	            System.out.println(axiomType.toString());
	            if (!axioms.isEmpty()) {
	                jsonObject.put(axiomType.toString(), convertAxiomSetToJSONArray(axioms));
	            }
	        }
	        String jsonString = jsonObject.toString();
	        System.out.println(jsonString);
	        return jsonString;
	        }

	        private static JSONArray convertAxiomSetToJSONArray(Set<? extends OWLAxiom> axiomSet) {
	            JSONArray jsonArray = new JSONArray();
	            for (OWLAxiom axiom : axiomSet) {
	                jsonArray.put(axiom.toString());
	            }
	            return jsonArray;
	        }
	        
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
	        
	        List<OWLAxiom> axioms = new ArrayList<>();
	        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
	            if (axiom instanceof OWLSubClassOfAxiom ||
	                axiom instanceof OWLDisjointClassesAxiom ||
	                axiom instanceof OWLEquivalentClassesAxiom ||
	                axiom instanceof OWLSubObjectPropertyOfAxiom ||
	                axiom instanceof OWLObjectPropertyDomainAxiom ||
	                axiom instanceof OWLObjectPropertyRangeAxiom ||
	                axiom instanceof OWLObjectPropertyAssertionAxiom ||
	                axiom instanceof OWLClassAssertionAxiom ||
	                axiom instanceof OWLDataPropertyAssertionAxiom) {
	                axioms.add(axiom);
	            }
	        }
	        
	        
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
		 
		 
		    public static class SameIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLSameIndividualAxiom> {

		        @Override
		        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSameIndividualAxiom> result) {
		            for (OWLNamedIndividual i : reasoner.getSameIndividuals(entity).getEntities()) {
		                if (!entity.equals(i)) {
		                    result.add(dataFactory.getOWLSameIndividualAxiom(entity, i));
		                }
		            }
		        }

				@Override
				public String getLabel() {
					// TODO Auto-generated method stub
					return "Same individual axioms";
				}
		    }
		    
		    public class InferredEquivalentClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLEquivalentClassesAxiom> result) {
		            Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(entity).getEntities();
		            if (equivalentClasses.size() > 1) {
		                result.add(dataFactory.getOWLEquivalentClassesAxiom(equivalentClasses));
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Equivalent classes";
		        }
		    }
		    
		    public class InferredDifferentIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLDifferentIndividualsAxiom> {

		        @Override
		        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDifferentIndividualsAxiom> result) {
		        	Set<OWLNamedIndividual> differentIndividuals = reasoner.getDifferentIndividuals(entity).getFlattened();
		            if (!differentIndividuals.isEmpty()) {
		            	result.add(dataFactory.getOWLDifferentIndividualsAxiom(Stream.concat(Stream.of(entity), differentIndividuals.stream()).toArray(OWLIndividual[]::new)));

		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Different individuals";
		        }
		    }
		    	
		    public class InferredIntersectionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLEquivalentClassesAxiom> result) {
		            NodeSet<OWLClass> directSuperClasses = reasoner.getSuperClasses(entity, true);
		            if (directSuperClasses.isEmpty()) {
		                return;
		            }

		            Set<OWLClassExpression> operands = new HashSet<>();
		            for (Node<OWLClass> superClassNode : directSuperClasses.getNodes()) {
		                operands.add(superClassNode.getRepresentativeElement());
		            }

		            if (operands.size() > 1) {
		                OWLObjectIntersectionOf intersection = dataFactory.getOWLObjectIntersectionOf(operands);
		                OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(entity, intersection);
		                result.add(axiom);
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Inferred Intersection Of";
		        }
		    }
		    
		    public class InferredUnionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {
		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            Set<OWLClass> directSuperclasses = reasoner.getSuperClasses(entity, true).getFlattened();
		            if (directSuperclasses.size() > 1) {
		                result.add(dataFactory.getOWLSubClassOfAxiom(entity, dataFactory.getOWLObjectUnionOf(directSuperclasses)));
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Inferred Union Of";
		        }
		    }
		    

		    public class InferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDisjointClassesAxiom> result) {
		            Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();
		            for (OWLClass cls : allClasses) {
		                if (!cls.equals(entity)) {
		                	Set<OWLClass> theEQ = reasoner.getEquivalentClasses(entity).getEntities();
		                    NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(entity);
		                    if (disjointClasses.containsEntity(cls)) {
		                    	
		                        Set<OWLClass> equivalentClasses1 = reasoner.getEquivalentClasses(entity).getEntities();
		                        Set<OWLClass> equivalentClasses2 = reasoner.getEquivalentClasses(cls).getEntities();
		                        for (OWLClass eqClass1 : equivalentClasses1) {
		                            for (OWLClass eqClass2 : equivalentClasses2) {
		                                if (!eqClass1.equals(eqClass2)) {
		                                    result.add(dataFactory.getOWLDisjointClassesAxiom(eqClass1, eqClass2));
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Disjoint classes";
		        }
		    }
		    
		    public class InferredSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		        @Override
		        protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            for (OWLClassExpression superClass : reasoner.getEquivalentClasses(cls).getEntities()) {
		                if (superClass.isAnonymous() && superClass instanceof OWLObjectSomeValuesFrom) {
		                    OWLObjectSomeValuesFrom someValuesFrom = (OWLObjectSomeValuesFrom) superClass;
		                    OWLClassExpression filler = someValuesFrom.getFiller();
		                    if (!filler.isAnonymous()) {
		                        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, someValuesFrom);
		                        result.add(axiom);
		                    }
		                }
		            }
		        }

		        public Set<InferredAxiomGenerator<?>> getInferredAxiomGenerators() {
		            return Collections.emptySet(); // No nested inferred axiom generators
		        }

		        @Override
		        public String getLabel() {
		            return "Some values from";
		        }
		    }
		    
		    public class InferredHasValueAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		        @Override
		        protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            for (OWLClassExpression equivalentClass : reasoner.getEquivalentClasses(cls).getEntities()) {
		                if (equivalentClass.isAnonymous() && equivalentClass instanceof OWLObjectHasValue) {
		                    OWLObjectHasValue hasValue = (OWLObjectHasValue) equivalentClass;
		                    OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, hasValue);
		                    result.add(axiom);
		                }
		            }
		        }

		        public Set<InferredAxiomGenerator<?>> getInferredAxiomGenerators() {
		            return Collections.emptySet(); // No nested inferred axiom generators
		        }

		        @Override
		        public String getLabel() {
		            return "Has value";
		        }
		    }
		    
		    public class InferredInverseObjectPropertiesAxiomGenerator implements InferredAxiomGenerator<OWLInverseObjectPropertiesAxiom> {

		        @Override
		        public Set<OWLInverseObjectPropertiesAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
		            Set<OWLInverseObjectPropertiesAxiom> result = new HashSet<>();
		            for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
		                addAxioms(objectProperty, reasoner, dataFactory, result);
		            }
		            return result;
		        }

		        protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLInverseObjectPropertiesAxiom> result) {
		            for (OWLObjectPropertyExpression inverse : reasoner.getInverseObjectProperties(entity).getEntities()) {
		                if (!inverse.equals(entity)) {
		                    result.add(dataFactory.getOWLInverseObjectPropertiesAxiom(entity, inverse));
		                }
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Inverse object properties";
		        }
		    }
		    
		    public class InferredAllValuesFromAxiomGenerator implements InferredAxiomGenerator<OWLSubClassOfAxiom> {

		        @Override
		        public Set<OWLSubClassOfAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
		            Set<OWLSubClassOfAxiom> result = new HashSet<>();
		            for (OWLClass owlClass : reasoner.getRootOntology().getClassesInSignature()) {
		                addAxioms(owlClass, reasoner, dataFactory, result);
		            }
		            return result;
		        }

		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
		                NodeSet<OWLClass> possibleRanges = reasoner.getObjectPropertyRanges(objectProperty, true);
		                if (!possibleRanges.isEmpty()) {
		                    OWLClassExpression allValuesFrom = dataFactory.getOWLObjectAllValuesFrom(objectProperty, possibleRanges.getFlattened().iterator().next());
		                    OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entity, allValuesFrom);
		                    result.add(axiom);
		                }
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "All values from";
		        }
		    }
		    
		    public class InferredSameValueSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            for (OWLNamedIndividual individual : reasoner.getRootOntology().getIndividualsInSignature()) {
		                for (OWLDataProperty dataProperty : reasoner.getRootOntology().getDataPropertiesInSignature()) {
		                    Set<OWLLiteral> literals = reasoner.getDataPropertyValues(individual, dataProperty);
		                    for (OWLLiteral literal : literals) {
		                        OWLClassExpression someValuesFrom = dataFactory.getOWLDataHasValue(dataProperty, literal);
		                        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entity, someValuesFrom);
		                        result.add(axiom);
		                    }
		                }
		                for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
		                    for (OWLNamedIndividual value : reasoner.getObjectPropertyValues(individual, objectProperty).getFlattened()) {
		                        OWLClassExpression someValuesFrom = dataFactory.getOWLObjectHasValue(objectProperty, value);
		                        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entity, someValuesFrom);
		                        result.add(axiom);
		                    }
		                }
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Same value implies some values from";
		        }
		    }
		    
		    public class InferredDomainAndRangeAxiomGenerator implements InferredAxiomGenerator<OWLObjectPropertyAxiom> {

		        @Override
		        public Set<OWLObjectPropertyAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
		            Set<OWLObjectPropertyAxiom> result = new HashSet<>();

		            for (OWLObjectProperty property : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
		                // Add inferred domain axioms
		                for (OWLClass domain : reasoner.getObjectPropertyDomains(property, true).getFlattened()) {
		                    result.add(dataFactory.getOWLObjectPropertyDomainAxiom(property, domain));
		                }

		                // Add inferred range axioms
		                for (OWLClass range : reasoner.getObjectPropertyRanges(property, true).getFlattened()) {
		                    result.add(dataFactory.getOWLObjectPropertyRangeAxiom(property, range));
		                }
		            }

		            return result;
		        }

		        @Override
		        public String getLabel() {
		            return "Inferred domains and ranges";
		        }
		    }

		   

}



