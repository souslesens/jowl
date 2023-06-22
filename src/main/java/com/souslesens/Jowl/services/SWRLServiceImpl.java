package com.souslesens.Jowl.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.gson.Gson;
import com.souslesens.Jowl.model.SWRLTypeEntityVariable;
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
				SWRLVariable var = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
				Set<SWRLAtom> bodyList = new HashSet<>();
				for (String bodies : reqBodies) {
					OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+bodies));
					SWRLClassAtom bodyElement = factory.getSWRLClassAtom(classX, var);
					bodyList.add(bodyElement);
				}
				Set<OWLClass> classes = new HashSet<>();
				Set<SWRLAtom> headList = new HashSet<>();
				for (String headies : reqHead) {
					OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+headies));
					classes.add(classX);
					SWRLClassAtom headElement = factory.getSWRLClassAtom(classX, var);
					headList.add(headElement);
				}
				
				SWRLRule rule = factory.getSWRLRule(bodyList, headList);
				AddAxiom addAxiom = new AddAxiom(ontology, rule);
				manager.applyChange(addAxiom);
				PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
				OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
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
	public String SWRLruleReclassificationB64(String ontologyContentDecoded64 ,  String[] reqBodies , String[] reqHead)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
			//set silent imports
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
			config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			manager.setOntologyLoaderConfiguration(config);
			OWLOntology ontology = null;
			System.out.println(instream.toString().isEmpty() == false);
			if (instream.available() > 0) {
				OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
				ontology = manager.loadOntologyFromOntologyDocument(documentSource);
			}
		// RULE : N { BODY } (x) -> N { Head } (x)

		OWLDataFactory factory = manager.getOWLDataFactory();
		// Create SWRL Variable for classification
		SWRLVariable var = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));
		Set<SWRLAtom> bodyList = new HashSet<>();
		for (String bodies : reqBodies) {
			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+bodies));
			SWRLClassAtom bodyElement = factory.getSWRLClassAtom(classX, var);
			bodyList.add(bodyElement);
		}
		Set<OWLClass> classes = new HashSet<>();
		Set<SWRLAtom> headList = new HashSet<>();
		for (String headies : reqHead) {
			OWLClass classX = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+headies));
			classes.add(classX);
			SWRLClassAtom headElement = factory.getSWRLClassAtom(classX, var);
			headList.add(headElement);
		}
		
		SWRLRule rule = factory.getSWRLRule(bodyList, headList);
		AddAxiom addAxiom = new AddAxiom(ontology, rule);
		manager.applyChange(addAxiom);

		// Creation reasoner 
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//
		// Computing inferences
		reasoner.precomputeInferences(InferenceType.values());

        Map<String, Set<String>> instances = new HashMap<>();
		for (OWLClass cls : classes) {
			NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false);
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
	public String SWRLruleVAB64(String ontologyContentDecoded64 , List<SWRLTypeEntityVariable> reqBodies , List<SWRLTypeEntityVariable> reqHead)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
			//set silent imports
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
			config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			manager.setOntologyLoaderConfiguration(config);
			OWLOntology ontology = null;
			if (instream.available() > 0) {
				OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
				ontology = manager.loadOntologyFromOntologyDocument(documentSource);
			}

		// RULE : N { BODY } (x) || N { BODY } (x,y) -> N { Head } (x) || N { BODY } (x,y)

		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<SWRLAtom> bodyList = new HashSet<>();
	
    	for (SWRLTypeEntityVariable swrlVariable1 : reqBodies) {
    	    if( swrlVariable1.getType().equalsIgnoreCase("owl:Class")){
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLClass classX =factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		 String[] variables = entity.getVar();
    	    		 String[] literals = entity.getLiteral();
    	    	        for (String table : variables ) {
    	    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));
    	    	        		SWRLClassAtom bodyElement = factory.getSWRLClassAtom(classX, swrlVar);
    	    	        		bodyList.add(bodyElement);
    	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:ObjectProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLObjectProperty ObjectPropertyX =factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
   	    		 String[] variables = entity.getVar();
	    	        if (variables.length != 2) {
	    	            throw new IllegalArgumentException("The variable list must contains Variable");
	    	        }
	    	        for (int v = 0 ; v<variables.length; v+=2) {
	    	            String variable1 = variables[v];
	    	            String variable2 = variables[v + 1];
	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
	    	            SWRLObjectPropertyAtom bodyElement = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, swrlVar1, swrlVar2);
	    	            bodyList.add(bodyElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:DataProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLDataProperty dataPropertyVar = factory.getOWLDataProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
   	    		 String[] variables = entity.getVar();
	    	        if (variables.length != 2) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        for (int v = 0 ; v<variables.length; v+=2) {
	    	            String variable1 = variables[v];
	    	            String variable2 = variables[v + 1];    	            
	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
	    	            SWRLDataPropertyAtom  bodyElement = factory.getSWRLDataPropertyAtom(dataPropertyVar, swrlVar1, swrlVar2);
	    	            System.out.println(bodyElement);
	    	            bodyList.add(bodyElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("swrlb:compare")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		
   	    		 String[] variables = entity.getVar();
   	    		 String[] literal = entity.getLiteral();
   	    		 
	    	        if (variables.length != 1) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        if (literal.length != 1) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        for (String table : variables ) {
	    	        	System.out.println("gregerg"+table);
    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));   	        		
	    	        	for (String Lit : literal) {
	    	        		System.out.println("gregerg"+Lit);
	    	        		OWLLiteral LitVar;
	    	        		if(Lit.matches("\\d+")) {
	    	        			int covertedValue = Integer.parseInt(Lit);
	    	        			System.out.println(covertedValue);
	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
	    	        			else if (Lit.matches("^\\d*\\.?\\d+$")){
	    	        				float covertedValue = Float.parseFloat(Lit);
	    	        				System.out.println(covertedValue);
		    	        			LitVar = factory.getOWLLiteral(covertedValue);
	    	        			}
	    	        			
	    	        			else {
	    	        				LitVar = factory.getOWLLiteral(Lit);
	    	        		}
	    	        		
	    	        		SWRLLiteralArgument LitVarArg = factory.getSWRLLiteralArgument(LitVar);
	    	        		List<SWRLDArgument> arguments = Arrays.asList(swrlVar, LitVarArg);    	  
	    	        		SWRLBuiltInAtom bodyElement = null;
	    	        		if(entity.getName().equalsIgnoreCase("greaterThanOrEqual")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI(), arguments);
	    	        		}else if (entity.getName().equalsIgnoreCase("lessThanOrEqual")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI(), arguments);
	    	        		}else if(entity.getName().equalsIgnoreCase("greaterThan")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN.getIRI(), arguments);	
	    	        		}else if(entity.getName().equalsIgnoreCase("lessThan")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN.getIRI(), arguments);		
	    	        		}else if(entity.getName().equalsIgnoreCase("equal")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.EQUAL.getIRI(), arguments);	
	    	        		}else if(entity.getName().equalsIgnoreCase("notEqual")) {
	    	        			bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.NOT_EQUAL.getIRI(), arguments);	
	    	        		}
	    	        		 System.out.println(bodyElement);
	    	        		bodyList.add(bodyElement);
	    	        	}
	    	        	
    	        		
	    	        }
    	    	}
    	    }else if(swrlVariable1.getType().equalsIgnoreCase("swrlb:string")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		
      	    		 String[] variables = entity.getVar();
      	    		 String[] literal = entity.getLiteral();
      	    		 
   	    	        if (variables.length != 1) {
   	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
   	    	        }
//   	    	        if (literal.length != 1) {
//   	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
//   	    	        }
   	    	        
   	    	        for (String table : variables ) {
   	    	        	System.out.println("gregerg"+table);
       	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));   	        		
       	        		SWRLBuiltInAtom bodyElement = null;
       	        		if(literal.length == 1){
       	        		for (String Lit : literal) {
   	    	        		System.out.println("gregerg"+Lit);
   	    	        		OWLLiteral LitVar;
   	    	        		if(Lit.matches("\\d+")) {
   	    	        			int covertedValue = Integer.parseInt(Lit);
   	    	        			System.out.println(covertedValue);
   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	    	        			else if (Lit.matches("^\\d*\\.?\\d+$")){
   	    	        				float covertedValue = Float.parseFloat(Lit);
   	    	        				System.out.println(covertedValue);
   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	    	        			}
   	    	        			
   	    	        			else {
   	    	        				LitVar = factory.getOWLLiteral(Lit);
   	    	        		}
   	    	        		System.out.println("ya VIPER"+LitVar);
   	    	        		SWRLLiteralArgument LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	    	        		System.out.println("WTFMAN"+LitVarArg);
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar, LitVarArg));    	    	    	        		
   	    	        		if(entity.getName().equalsIgnoreCase("contains")) {
   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.CONTAINS.getIRI(), arguments);
   	    	        		}else if (entity.getName().equalsIgnoreCase("containsIgnoreCase")) {
   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.CONTAINS_IGNORE_CASE.getIRI(), arguments);
   	    	        		}else if (entity.getName().equalsIgnoreCase("startsWith")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.STARTS_WITH.getIRI(), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("endsWith")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.ENDS_WITH.getIRI(), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("matches")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((IRI.create("http://www.w3.org/2003/11/swrlb#matches")), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("stringEqualIgnoreCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_EQUALS_IGNORE_CASE.getIRI()), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("upperCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((IRI.create("http://www.w3.org/2003/11/swrlb#upperCase")), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("lowerCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.LOWER_CASE.getIRI()), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("stringLength")) {	
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_LENGTH.getIRI()) , arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("normalizeSpace")) {	
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.NORMALIZE_SPACE.getIRI()) , arguments);
   	   	    	        	}
       	        		}
//   	   	    	        	else if(entity.getName().equalsIgnoreCase("stringConcat")) {
//   	   	    	        	OWLLiteral LitVar1 = factory.getOWLLiteral("ine");
//   	   	    	        	SWRLLiteralArgument LitVarArg1 = factory.getSWRLLiteralArgument(LitVar1);
//   	   	    	        	arguments.add(LitVarArg1);
//   	   	    	        	bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
//   	   	    	        	}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}else if(literal.length == 2) {
   	    	        		SWRLLiteralArgument LitVarArg = null;
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar));
   	    	        		for (int i = 0; i < literal.length; i++) {
   	    	        			OWLLiteral LitVar;
   	   	    	        		if(literal[i].matches("\\d+")) {
   	   	    	        			int covertedValue = Integer.parseInt(literal[i]);
   	   	    	        			System.out.println(covertedValue);
   	   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	   	    	        			else if (literal[i].matches("^\\d*\\.?\\d+$")){
   	   	    	        				float covertedValue = Float.parseFloat(literal[i]);
   	   	    	        				System.out.println(covertedValue);
   	   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	   	    	        			}
   	   	    	        			
   	   	    	        			else {
   	   	    	        				LitVar = factory.getOWLLiteral(literal[i]);
   	   	    	        		}
   	   	    	        		System.out.println(LitVar);
   	   	    	        		LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	   	    	        		System.out.println("AMINE"+LitVarArg);
   	   	    	        		
   	   	    	        		arguments.add(LitVarArg);
   	   	    	        		System.out.println(arguments);
   	   	    	        		if(entity.getName().equalsIgnoreCase("stringConcat")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substring")) {
   	   	    	        		
   	   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING.getIRI()) , arguments);
   	   	   	    	        	
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("tokenize")) {
   	   	    	        			bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.TOKENIZE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substringBefore")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING_BEFORE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substringAfter")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING_AFTER.getIRI()) , arguments);
   	   	    	        		}
   	   	    	        			
							}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}else if(literal.length > 2) {
   	    	        		SWRLLiteralArgument LitVarArg = null;
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar));
   	    	        		for (int i = 0; i < literal.length; i++) {
   	    	        			OWLLiteral LitVar;
   	   	    	        		if(literal[i].matches("\\d+")) {
   	   	    	        			int covertedValue = Integer.parseInt(literal[i]);
   	   	    	        			System.out.println(covertedValue);
   	   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	   	    	        			else if (literal[i].matches("^\\d*\\.?\\d+$")){
   	   	    	        				float covertedValue = Float.parseFloat(literal[i]);
   	   	    	        				System.out.println(covertedValue);
   	   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	   	    	        			}
   	   	    	        			
   	   	    	        			else {
   	   	    	        				LitVar = factory.getOWLLiteral(literal[i]);
   	   	    	        		}
   	   	    	        		System.out.println(LitVar);
   	   	    	        		LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	   	    	        		System.out.println("AMINE"+LitVarArg);
   	   	    	        		
   	   	    	        		arguments.add(LitVarArg);
   	   	    	        		System.out.println(arguments);
   	   	    	        		if(entity.getName().equalsIgnoreCase("stringConcat")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substring")) {
   	   	   	    	        	bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING.getIRI()) , arguments); 	   	    	        	
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("replace")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.REPLACE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("translate")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.TRANSLATE.getIRI()) , arguments);
   	   	    	        		}
   	   	    	        			
							}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}
   	    	        	
       	        		
   	    	        }
       	    	}
    	    }
    	    System.out.println();
    	}
		
		Set<OWLClass> classes = new HashSet<>();
		Set<OWLObjectProperty> objectproperties = new HashSet<>();
		Set<SWRLAtom> headList = new HashSet<>();
    	for (SWRLTypeEntityVariable swrlVariable1 : reqHead) {
    	    if( swrlVariable1.getType().equalsIgnoreCase("owl:Class")){
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLClass classX =factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		classes.add(classX);
   	    		 String[] variables = entity.getVar();
	    	        for (String table : variables ) {
	    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));
	    	        		SWRLClassAtom headElement = factory.getSWRLClassAtom(classX, swrlVar);
	    	        		headList.add(headElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:ObjectProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLObjectProperty ObjectPropertyX =factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		objectproperties.add(ObjectPropertyX);
    	    		 String[] variables = entity.getVar();
    	    	        if (variables.length != 2) {
    	    	            throw new IllegalArgumentException("The variable list must contain an even number of elements");
    	    	        }
    	    	        for (int v = 0 ; v<variables.length; v+=2) {
    	    	            String variable1 = variables[v];
    	    	            String variable2 = variables[v + 1];
    	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
    	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
    	    	            SWRLObjectPropertyAtom headElement = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, swrlVar1, swrlVar2);
    	    	            headList.add(headElement);
    	    	        }
    	    	}
    	    }
    	    System.out.println();
    	}
		
		SWRLRule rule = factory.getSWRLRule(bodyList, headList);
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
		for (OWLClass owlClass : classes) {
			
		    Set<OWLIndividual> assertedInstances = new HashSet<>();
		    for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(owlClass)) {
		        assertedInstances.add(axiom.getIndividual());
		    }
			
		    NodeSet<OWLNamedIndividual> allInstances  = reasoner.getInstances(owlClass, false);
		    
		    Set<OWLNamedIndividual> inferredInstances = allInstances.getFlattened();
		    inferredInstances.removeAll(assertedInstances);
		    
		    for (OWLNamedIndividual inferredInstance  : inferredInstances) {
		        System.out.println(inferredInstance + " is an instance of " + owlClass);
		    }
		}
		Map<String, Set<String>> propertyValues = new HashMap<>();
		for (OWLObjectProperty objectproperty : objectproperties) {
		    for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
		        NodeSet<OWLNamedIndividual> objectPropertyValues = reasoner.getObjectPropertyValues(individual, objectproperty);

		        // If there are any values, add them to the map
		        for (OWLNamedIndividual value : objectPropertyValues.getFlattened()) {
		            String key = individual.getIRI().getFragment();
		            propertyValues.computeIfAbsent(key, k -> new HashSet<>()).add(objectproperty.getIRI().getFragment()+"/"+ value.getIRI().getFragment() );
		        }
		    }
		}
        Map<String, Set<String>> instances = new HashMap<>();
		for (OWLClass cls : classes) {
			NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false); // false = only inferred
            for (Node<OWLNamedIndividual> individualNode : inferredIndv) {
                for (OWLNamedIndividual individual : individualNode) {
                	instances.computeIfAbsent(individual.getIRI().getFragment(), k -> new HashSet<>()).add(cls.getIRI().getFragment());
                }
            }
		}
		Map<String, Map<String, Set<String>>> finalMap = new HashMap<>();
		finalMap.put("class", instances);
		finalMap.put("objectProperty", propertyValues);
		 Gson gson = new Gson();
         String json = gson.toJson(finalMap);
		 return json;
	}catch (OWLOntologyCreationException e){
		Gson gson = new Gson();
		String json = gson.toJson(e);
		return json;
	}


	}

	@Override
	public String SWRLruleVABUF(String filePath, String url, List<SWRLTypeEntityVariable> reqBodies,
			List<SWRLTypeEntityVariable> reqHead)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
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

		// RULE : N { BODY } (x) || N { BODY } (x,y) -> N { Head } (x) || N { BODY } (x,y)

		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<SWRLAtom> bodyList = new HashSet<>();
	
    	for (SWRLTypeEntityVariable swrlVariable1 : reqBodies) {
    	    if( swrlVariable1.getType().equalsIgnoreCase("owl:Class")){
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLClass classX =factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		 String[] variables = entity.getVar();
    	    		 String[] literals = entity.getLiteral();
    	    	        for (String table : variables ) {
    	    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));
    	    	        		SWRLClassAtom bodyElement = factory.getSWRLClassAtom(classX, swrlVar);
    	    	        		bodyList.add(bodyElement);
    	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:ObjectProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLObjectProperty ObjectPropertyX =factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
   	    		 String[] variables = entity.getVar();
	    	        if (variables.length != 2) {
	    	            throw new IllegalArgumentException("The variable list must contains Variable");
	    	        }
	    	        for (int v = 0 ; v<variables.length; v+=2) {
	    	            String variable1 = variables[v];
	    	            String variable2 = variables[v + 1];
	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
	    	            SWRLObjectPropertyAtom bodyElement = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, swrlVar1, swrlVar2);
	    	            bodyList.add(bodyElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:DataProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLDataProperty dataPropertyVar = factory.getOWLDataProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
   	    		 String[] variables = entity.getVar();
	    	        if (variables.length != 2) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        for (int v = 0 ; v<variables.length; v+=2) {
	    	            String variable1 = variables[v];
	    	            String variable2 = variables[v + 1];    	            
	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
	    	            SWRLDataPropertyAtom  bodyElement = factory.getSWRLDataPropertyAtom(dataPropertyVar, swrlVar1, swrlVar2);
	    	            System.out.println(bodyElement);
	    	            bodyList.add(bodyElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("swrlb:compare")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		
   	    		 String[] variables = entity.getVar();
   	    		 String[] literal = entity.getLiteral();
   	    		 
	    	        if (variables.length != 1) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        if (literal.length != 1) {
	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
	    	        }
	    	        for (String table : variables ) {
	    	        	System.out.println("gregerg"+table);
    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));   	        		
	    	        	for (String Lit : literal) {
	    	        		System.out.println("gregerg"+Lit);
	    	        		OWLLiteral LitVar;
	    	        		if(Lit.matches("\\d+")) {
	    	        			int covertedValue = Integer.parseInt(Lit);
	    	        			System.out.println(covertedValue);
	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
	    	        			else if (Lit.matches("^\\d*\\.?\\d+$")){
	    	        				float covertedValue = Float.parseFloat(Lit);
	    	        				System.out.println(covertedValue);
		    	        			LitVar = factory.getOWLLiteral(covertedValue);
	    	        			}
	    	        			
	    	        			else {
	    	        				LitVar = factory.getOWLLiteral(Lit);
	    	        		}
	    	        		
	    	        		SWRLLiteralArgument LitVarArg = factory.getSWRLLiteralArgument(LitVar);
	    	        		List<SWRLDArgument> arguments = Arrays.asList(swrlVar, LitVarArg);    	  
	    	        		SWRLBuiltInAtom bodyElement = null;
	    	        		if(entity.getName().equalsIgnoreCase("greaterThanOrEqual")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI(), arguments);
	    	        		}else if (entity.getName().equalsIgnoreCase("lessThanOrEqual")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI(), arguments);
	    	        		}else if(entity.getName().equalsIgnoreCase("greaterThan")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.GREATER_THAN.getIRI(), arguments);	
	    	        		}else if(entity.getName().equalsIgnoreCase("lessThan")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.LESS_THAN.getIRI(), arguments);		
	    	        		}else if(entity.getName().equalsIgnoreCase("equal")) {
	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.EQUAL.getIRI(), arguments);	
	    	        		}else if(entity.getName().equalsIgnoreCase("notEqual")) {
	    	        			bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.NOT_EQUAL.getIRI(), arguments);	
	    	        		}
	    	        		 System.out.println(bodyElement);
	    	        		bodyList.add(bodyElement);
	    	        	}
	    	        	
    	        		
	    	        }
    	    	}
    	    }else if(swrlVariable1.getType().equalsIgnoreCase("swrlb:string")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		
      	    		 String[] variables = entity.getVar();
      	    		 String[] literal = entity.getLiteral();
      	    		 
   	    	        if (variables.length != 1) {
   	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
   	    	        }
//   	    	        if (literal.length != 1) {
//   	    	            throw new IllegalArgumentException("The variable list must contains 2 arguemts");
//   	    	        }
   	    	        
   	    	        for (String table : variables ) {
   	    	        	System.out.println("gregerg"+table);
       	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));   	        		
       	        		SWRLBuiltInAtom bodyElement = null;
       	        		if(literal.length == 1){
       	        		for (String Lit : literal) {
   	    	        		System.out.println("gregerg"+Lit);
   	    	        		OWLLiteral LitVar;
   	    	        		if(Lit.matches("\\d+")) {
   	    	        			int covertedValue = Integer.parseInt(Lit);
   	    	        			System.out.println(covertedValue);
   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	    	        			else if (Lit.matches("^\\d*\\.?\\d+$")){
   	    	        				float covertedValue = Float.parseFloat(Lit);
   	    	        				System.out.println(covertedValue);
   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	    	        			}
   	    	        			
   	    	        			else {
   	    	        				LitVar = factory.getOWLLiteral(Lit);
   	    	        		}
   	    	        		System.out.println("ya VIPER"+LitVar);
   	    	        		SWRLLiteralArgument LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	    	        		System.out.println("WTFMAN"+LitVarArg);
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar, LitVarArg));    	    	    	        		
   	    	        		if(entity.getName().equalsIgnoreCase("contains")) {
   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.CONTAINS.getIRI(), arguments);
   	    	        		}else if (entity.getName().equalsIgnoreCase("containsIgnoreCase")) {
   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.CONTAINS_IGNORE_CASE.getIRI(), arguments);
   	    	        		}else if (entity.getName().equalsIgnoreCase("startsWith")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.STARTS_WITH.getIRI(), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("endsWith")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom(SWRLBuiltInsVocabulary.ENDS_WITH.getIRI(), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("matches")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((IRI.create("http://www.w3.org/2003/11/swrlb#matches")), arguments);
   	   	    	        	}else if (entity.getName().equalsIgnoreCase("stringEqualIgnoreCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_EQUALS_IGNORE_CASE.getIRI()), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("upperCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((IRI.create("http://www.w3.org/2003/11/swrlb#upperCase")), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("lowerCase")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.LOWER_CASE.getIRI()), arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("stringLength")) {	
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_LENGTH.getIRI()) , arguments);
   	   	    	        	}else if(entity.getName().equalsIgnoreCase("normalizeSpace")) {	
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.NORMALIZE_SPACE.getIRI()) , arguments);
   	   	    	        	}
       	        		}
//   	   	    	        	else if(entity.getName().equalsIgnoreCase("stringConcat")) {
//   	   	    	        	OWLLiteral LitVar1 = factory.getOWLLiteral("ine");
//   	   	    	        	SWRLLiteralArgument LitVarArg1 = factory.getSWRLLiteralArgument(LitVar1);
//   	   	    	        	arguments.add(LitVarArg1);
//   	   	    	        	bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
//   	   	    	        	}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}else if(literal.length == 2) {
   	    	        		SWRLLiteralArgument LitVarArg = null;
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar));
   	    	        		for (int i = 0; i < literal.length; i++) {
   	    	        			OWLLiteral LitVar;
   	   	    	        		if(literal[i].matches("\\d+")) {
   	   	    	        			int covertedValue = Integer.parseInt(literal[i]);
   	   	    	        			System.out.println(covertedValue);
   	   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	   	    	        			else if (literal[i].matches("^\\d*\\.?\\d+$")){
   	   	    	        				float covertedValue = Float.parseFloat(literal[i]);
   	   	    	        				System.out.println(covertedValue);
   	   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	   	    	        			}
   	   	    	        			
   	   	    	        			else {
   	   	    	        				LitVar = factory.getOWLLiteral(literal[i]);
   	   	    	        		}
   	   	    	        		System.out.println(LitVar);
   	   	    	        		LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	   	    	        		System.out.println("AMINE"+LitVarArg);
   	   	    	        		
   	   	    	        		arguments.add(LitVarArg);
   	   	    	        		System.out.println(arguments);
   	   	    	        		if(entity.getName().equalsIgnoreCase("stringConcat")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substring")) {
   	   	    	        		
   	   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING.getIRI()) , arguments);
   	   	   	    	        	
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("tokenize")) {
   	   	    	        			bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.TOKENIZE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substringBefore")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING_BEFORE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substringAfter")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING_AFTER.getIRI()) , arguments);
   	   	    	        		}
   	   	    	        			
							}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}else if(literal.length > 2) {
   	    	        		SWRLLiteralArgument LitVarArg = null;
   	    	        		List<SWRLDArgument> arguments = new ArrayList<>(Arrays.asList(swrlVar));
   	    	        		for (int i = 0; i < literal.length; i++) {
   	    	        			OWLLiteral LitVar;
   	   	    	        		if(literal[i].matches("\\d+")) {
   	   	    	        			int covertedValue = Integer.parseInt(literal[i]);
   	   	    	        			System.out.println(covertedValue);
   	   	    	        			LitVar = factory.getOWLLiteral(covertedValue);}
   	   	    	        			else if (literal[i].matches("^\\d*\\.?\\d+$")){
   	   	    	        				float covertedValue = Float.parseFloat(literal[i]);
   	   	    	        				System.out.println(covertedValue);
   	   		    	        			LitVar = factory.getOWLLiteral(covertedValue);
   	   	    	        			}
   	   	    	        			
   	   	    	        			else {
   	   	    	        				LitVar = factory.getOWLLiteral(literal[i]);
   	   	    	        		}
   	   	    	        		System.out.println(LitVar);
   	   	    	        		LitVarArg = factory.getSWRLLiteralArgument(LitVar);
   	   	    	        		System.out.println("AMINE"+LitVarArg);
   	   	    	        		
   	   	    	        		arguments.add(LitVarArg);
   	   	    	        		System.out.println(arguments);
   	   	    	        		if(entity.getName().equalsIgnoreCase("stringConcat")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.STRING_CONCAT.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("substring")) {
   	   	   	    	        	bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.SUBSTRING.getIRI()) , arguments); 	   	    	        	
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("replace")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.REPLACE.getIRI()) , arguments);
   	   	    	        		}else if(entity.getName().equalsIgnoreCase("translate")) {
   	   	    	        		bodyElement = factory.getSWRLBuiltInAtom((SWRLBuiltInsVocabulary.TRANSLATE.getIRI()) , arguments);
   	   	    	        		}
   	   	    	        			
							}
   	    	        		System.out.println(bodyElement);
   	    	        		bodyList.add(bodyElement);
   	    	        	}
   	    	        	
       	        		
   	    	        }
       	    	}
    	    }
    	    System.out.println();
    	}
		
		Set<OWLClass> classes = new HashSet<>();
		Set<OWLObjectProperty> objectproperties = new HashSet<>();
		Set<SWRLAtom> headList = new HashSet<>();
    	for (SWRLTypeEntityVariable swrlVariable1 : reqHead) {
    	    if( swrlVariable1.getType().equalsIgnoreCase("owl:Class")){
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLClass classX =factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		classes.add(classX);
   	    		 String[] variables = entity.getVar();
	    	        for (String table : variables ) {
	    	        		SWRLVariable swrlVar = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+table));
	    	        		SWRLClassAtom headElement = factory.getSWRLClassAtom(classX, swrlVar);
	    	        		headList.add(headElement);
	    	        }
    	    	}
    	    }else if (swrlVariable1.getType().equalsIgnoreCase("owl:ObjectProperty")) {
    	    	for (SWRLVariables entity : swrlVariable1.getEntities()) {
    	    		OWLObjectProperty ObjectPropertyX =factory.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#"+entity.getName()));
    	    		objectproperties.add(ObjectPropertyX);
    	    		 String[] variables = entity.getVar();
    	    	        if (variables.length != 2) {
    	    	            throw new IllegalArgumentException("The variable list must contain an even number of elements");
    	    	        }
    	    	        for (int v = 0 ; v<variables.length; v+=2) {
    	    	            String variable1 = variables[v];
    	    	            String variable2 = variables[v + 1];
    	    	            SWRLVariable swrlVar1 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable1));
    	    	            SWRLVariable swrlVar2 = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + variable2));
    	    	            SWRLObjectPropertyAtom headElement = factory.getSWRLObjectPropertyAtom(ObjectPropertyX, swrlVar1, swrlVar2);
    	    	            headList.add(headElement);
    	    	        }
    	    	}
    	    }
    	    System.out.println();
    	}
		
		SWRLRule rule = factory.getSWRLRule(bodyList, headList);
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
		for (OWLClass owlClass : classes) {
			
		    Set<OWLIndividual> assertedInstances = new HashSet<>();
		    for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(owlClass)) {
		        assertedInstances.add(axiom.getIndividual());
		    }
			
		    NodeSet<OWLNamedIndividual> allInstances  = reasoner.getInstances(owlClass, false);
		    
		    Set<OWLNamedIndividual> inferredInstances = allInstances.getFlattened();
		    inferredInstances.removeAll(assertedInstances);
		    
		    for (OWLNamedIndividual inferredInstance  : inferredInstances) {
		        System.out.println(inferredInstance + " is an instance of " + owlClass);
		    }
		}
		Map<String, Set<String>> propertyValues = new HashMap<>();
		for (OWLObjectProperty objectproperty : objectproperties) {
		    for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
		        NodeSet<OWLNamedIndividual> objectPropertyValues = reasoner.getObjectPropertyValues(individual, objectproperty);

		        // If there are any values, add them to the map
		        for (OWLNamedIndividual value : objectPropertyValues.getFlattened()) {
		            String key = individual.getIRI().getFragment();
		            propertyValues.computeIfAbsent(key, k -> new HashSet<>()).add(objectproperty.getIRI().getFragment()+"/"+ value.getIRI().getFragment() );
		        }
		    }
		}
        Map<String, Set<String>> instances = new HashMap<>();
		for (OWLClass cls : classes) {
			NodeSet<OWLNamedIndividual> inferredIndv = reasoner.getInstances(cls, false); // false = only inferred
            for (Node<OWLNamedIndividual> individualNode : inferredIndv) {
                for (OWLNamedIndividual individual : individualNode) {
                	instances.computeIfAbsent(individual.getIRI().getFragment(), k -> new HashSet<>()).add(cls.getIRI().getFragment());
                }
            }
		}
		Map<String, Map<String, Set<String>>> finalMap = new HashMap<>();
		finalMap.put("class", instances);
		finalMap.put("objectProperty", propertyValues);
		 Gson gson = new Gson();
         String json = gson.toJson(finalMap);
		 return json;
	}catch (OWLOntologyCreationException e){
		Gson gson = new Gson();
		String json = gson.toJson(e);
		return json;
	}


	}

}
