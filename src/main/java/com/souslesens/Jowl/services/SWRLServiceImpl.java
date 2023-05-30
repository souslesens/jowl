package com.souslesens.Jowl.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.gson.Gson;
import com.souslesens.Jowl.model.SWRLVariable1;
import com.souslesens.Jowl.model.SWRLVariables;
@Service

public class SWRLServiceImpl implements SWRLService {


	@Override
	public String SWRLruleReclassification(String filePath, String url, String[] reqBodies , String[] reqHead) throws Exception {
		try {
			
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {

			ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
		} else if (filePath.isEmpty() == false && url == null) {

			ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		} else {
			return null;
		}
		// BLOC //
		
				// RULE : N { BODY } (x) -> N { Head } (x)

				OWLDataFactory factory = manager.getOWLDataFactory();
				// Create SWRL Variable for classification
				SWRLVariable var = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
				Set<SWRLAtom> body = new HashSet<>();
				for (String bodies : reqBodies) {
					OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+bodies));
					SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, var);
					body.add(body2);
				}
				Set<OWLClass> classes = new HashSet<>();
				Set<SWRLAtom> head = new HashSet<>();
				for (String headies : reqHead) {
					OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+headies));
					classes.add(classX);
					SWRLClassAtom head2 = factory.getSWRLClassAtom(classX, var);
					head.add(head2);
				}
				
				SWRLRule rule = factory.getSWRLRule(body, head);
				AddAxiom addAxiom = new AddAxiom(ontology, rule);
				// Add SWRL rule to ontology
				manager.applyChange(addAxiom);

				// Creation reasoner 
				PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
				OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
				//
				// Computing inferences
				reasoner.precomputeInferences(InferenceType.values());

		        Map<String, Set<String>> instances = new HashMap<>();
				for (OWLClass cls : classes) {
					NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false); // false = only inferred
		            for (Node<OWLNamedIndividual> individualNode : inferredIndv) {
		                for (OWLNamedIndividual individual : individualNode) {
		                	System.out.println(individual.getIRI().getFragment() + " is an instance of " + cls.getIRI().getFragment());
		                	instances.computeIfAbsent(individual.getIRI().getFragment(), k -> new HashSet<>()).add(cls.getIRI().getFragment());
		                }
		            }
				}
				 Gson gson = new Gson();
		         String json = gson.toJson(instances);
				 return json;
			}catch (OWLOntologyCreationException e){
				Gson gson = new Gson();
				String json = gson.toJson(e);
				return json;
			}
		
	}
	// TEST

	@Override
	public String SWRLruleReclassificationB64(String ontologyContentDecoded64 ,  String[] reqBodies , String[] reqHead)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		try {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		String filePath = null;
		if (ontologyContentDecoded64.isEmpty() == false) {
			InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
			try {
				Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
				filePath = "output.owl";

			} catch (IOException e) {
				e.printStackTrace();
			}
			ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
			if (filePath.isEmpty() == false) {

				ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
			}

		} else {
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
		// BLOC //
		
		// RULE : N { BODY } (x) -> N { Head } (x)

		OWLDataFactory factory = manager.getOWLDataFactory();
		// Create SWRL Variable for classification
		SWRLVariable var = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
		Set<SWRLAtom> body = new HashSet<>();
		for (String bodies : reqBodies) {
			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+bodies));
			SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, var);
			body.add(body2);
		}
		Set<OWLClass> classes = new HashSet<>();
		Set<SWRLAtom> head = new HashSet<>();
		for (String headies : reqHead) {
			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+headies));
			classes.add(classX);
			SWRLClassAtom head2 = factory.getSWRLClassAtom(classX, var);
			head.add(head2);
		}
		
		SWRLRule rule = factory.getSWRLRule(body, head);
		AddAxiom addAxiom = new AddAxiom(ontology, rule);
		// Add SWRL rule to ontology
		manager.applyChange(addAxiom);

		// Creation reasoner 
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//
		// Computing inferences
		reasoner.precomputeInferences(InferenceType.values());

        Map<String, Set<String>> instances = new HashMap<>();
		for (OWLClass cls : classes) {
			NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false); // false = only inferred
            for (Node<OWLNamedIndividual> individualNode : inferredIndv) {
                for (OWLNamedIndividual individual : individualNode) {
                	System.out.println(individual.getIRI().getFragment() + " is an instance of " + cls.getIRI().getFragment());
                	instances.computeIfAbsent(individual.getIRI().getFragment(), k -> new HashSet<>()).add(cls.getIRI().getFragment());
                }
            }
		}
		 Gson gson = new Gson();
         String json = gson.toJson(instances);
		 return json;
	}catch (OWLOntologyCreationException e){
		Gson gson = new Gson();
		String json = gson.toJson(e);
		return json;
	}


	}
	
	@Override
	public String SWRLruleVAB64(String ontologyContentDecoded64 , List<SWRLVariable1> reqBodies , String reqHead)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		try {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		String filePath = null;
		if (ontologyContentDecoded64.isEmpty() == false) {
			InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
			try {
				Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
				filePath = "output.owl";

			} catch (IOException e) {
				e.printStackTrace();
			}
			ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
			if (filePath.isEmpty() == false) {

				ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
			}

		} else {
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
		// BLOC //

		// RULE : N { BODY } (x) -> N { Head } (x)

		OWLDataFactory factory = manager.getOWLDataFactory();
		// Create SWRL Variable for classification
		
//		SWRLVariable varY = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#y"));
//		SWRLVariable varZ = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#z"));
		Set<SWRLAtom> body = new HashSet<>();
		///////////////////
//    	for (SWRLVariable1 swrlVariable1 : reqBodies) {
//    	    System.out.println("Type: " + swrlVariable1.getType());
//    	    for (SWRLVariables entity : swrlVariable1.getEntities()) {
//    	        System.out.println("Name: " + entity.getName());
//    	        System.out.println("Var: " + Arrays.toString(entity.getVar()));
//    	    }
//    	    System.out.println();
//    	}
		
		
		
    	for (SWRLVariable1 swrlVariable1 : reqBodies) {
    	    if( swrlVariable1.getType().equalsIgnoreCase("class")){
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLClass classX =factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
//    	    		SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, varX);
    	    		 String[] variables = entity.getVar();
    	    	        for (String table : variables ) {
    	    	        	if ( table.equalsIgnoreCase("x")) {
    	    	        		SWRLVariable varX = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
    	    	        		SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, varX);
    	    	        		body.add(body2);
    	    	        	}else if (table.equalsIgnoreCase("y")) {
    	    	        		SWRLVariable varY = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#y"));
    	    	        		SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, varY);
    	    	        		body.add(body2);
    	    	        	}else if (table.equalsIgnoreCase("z")) {
    	    	        		SWRLVariable varZ = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#z"));
    	    	        		SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, varZ);
    	    	        		body.add(body2);
    	    	        	}
    	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("objectProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
//    	    		System.out.println(entity);
    	    		OWLObjectProperty ObjectPropertyX =factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
//    	    		SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, varX);
    	    		 String[] variables = entity.getVar();
    	    		 String Var = "";
    	    	        for (String table : variables ) {
    	    	        	Var = Var+table;
    	    	        }
    	    	        if (Var.equalsIgnoreCase("XY") || Var.equalsIgnoreCase("YX")) {
	    	        		SWRLVariable varX = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
	    	        		SWRLVariable varY = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#y"));
	    	        		SWRLObjectPropertyAtom  body2 = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, varX , varY);
	    	        		body.add(body2);
    	    	        }else if(Var.equalsIgnoreCase("XZ") || Var.equalsIgnoreCase("ZX")) {
	    	        		SWRLVariable varX = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
	    	        		SWRLVariable varZ = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#z"));
	    	        		SWRLObjectPropertyAtom  body2 = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, varX , varZ);
	    	        		body.add(body2);
    	    	        }else if(Var.equalsIgnoreCase("YZ") || Var.equalsIgnoreCase("YZ") ) {
	    	        		SWRLVariable varY = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#y"));
	    	        		SWRLVariable varZ = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#z"));
	    	        		SWRLObjectPropertyAtom  body2 = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, varY , varZ);
	    	        		body.add(body2);
    	    	        }
    	    	}
    	    }
//    	    for (SWRLVariables entity : swrlVariable1.getEntities()) {
//    	        System.out.println("Name: " + entity.getName());
//    	        String[] variables = entity.getVar();
//    	        for (String table : variables ) {
//    	        	System.out.println("Var: " + table);
//    	        }
//    	        System.out.println("Var: " + Arrays.toString(entity.getVar()));
//    	    }
    	    System.out.println();
    	}
		
		
		////////////////////
//		for (String bodies : reqBodies) {
//			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+bodies));
//			SWRLClassAtom body2 = factory.getSWRLClassAtom(classX, var);
//			body.add(body2);
//		}
		Set<OWLClass> classes = new HashSet<>();
//		Set<SWRLAtom> head = new HashSet<>();
		// WORKING 1 ??
//		OWLClass classReqHead = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+reqHead));
		//??
		
		//working2 
		OWLObjectProperty objectPropertyReqHead = factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() +"#"+reqHead));
		//
		
		
		
		//		for (String headies : reqHead) {
//			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+headies));
//			classes.add(classX);
//			SWRLClassAtom head2 = factory.getSWRLClassAtom(classX, var);
//			head.add(head2);
//		}
		SWRLVariable varX = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
		SWRLVariable varY = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#y"));
//		SWRLClassAtom head = factory.getSWRLClassAtom(classReqHead, varX);
		SWRLObjectPropertyAtom head = factory.getSWRLObjectPropertyAtom(objectPropertyReqHead, varX, varY);
		SWRLRule rule = factory.getSWRLRule(body,Collections.singleton(head));
//		SWRLRule rule = factory.getSWRLRule(body, head);
		AddAxiom addAxiom = new AddAxiom(ontology, rule);
		// Add SWRL rule to ontology
		manager.applyChange(addAxiom);

		// Creation reasoner 
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//
		// Computing inferences
		reasoner.precomputeInferences(InferenceType.values());
		// Print out inferred instances
		// LAST 
        // Loop through each individual in the ontology
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            // Get the inferred cooperatedWith values
            NodeSet<OWLNamedIndividual> cooperatedWithValues = reasoner.getObjectPropertyValues(individual, objectPropertyReqHead);

            // If there are any values, print them
            if (!cooperatedWithValues.getFlattened().isEmpty()) {
                System.out.println(individual + " HasBrother:");

                for (OWLNamedIndividual value : cooperatedWithValues.getFlattened()) {
                    System.out.println("\t" + value);
                }
            }
        }
		
		
		// LAST
		
		
		//222222
//		for (OWLClass owlClass : ontology.getClassesInSignature()) {
//			
//		    Set<OWLIndividual> assertedInstances = new HashSet<>();
//		    for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(owlClass)) {
//		        assertedInstances.add(axiom.getIndividual());
//		    }
//			
//		    NodeSet<OWLNamedIndividual> allInstances  = reasoner.getInstances(owlClass, false);
//		    
//		    Set<OWLNamedIndividual> inferredInstances = allInstances.getFlattened();
//		    inferredInstances.removeAll(assertedInstances);
//		    
//		    for (OWLNamedIndividual inferredInstance  : inferredInstances) {
//		        System.out.println(inferredInstance + " is an instance of " + owlClass);
//		    }
//		}
		
        Map<String, Set<String>> instances = new HashMap<>();
		for (OWLClass cls : classes) {
			NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false); // false = only inferred
            for (Node<OWLNamedIndividual> individualNode : inferredIndv) {
                for (OWLNamedIndividual individual : individualNode) {
                	System.out.println(individual.getIRI().getFragment() + " is an instance of " + cls.getIRI().getFragment());
                	instances.computeIfAbsent(individual.getIRI().getFragment(), k -> new HashSet<>()).add(cls.getIRI().getFragment());
                }
            }
		}
		 Gson gson = new Gson();
         String json = gson.toJson(instances);
		 return json;
	}catch (OWLOntologyCreationException e){
		Gson gson = new Gson();
		String json = gson.toJson(e);
		return json;
	}


	}

}
