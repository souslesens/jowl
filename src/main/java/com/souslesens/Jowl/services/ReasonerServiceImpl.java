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

import org.coode.owlapi.owlxmlparser.OWLXMLParserFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory.OWLOntologyCreationHandler;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;

@Service

public class ReasonerServiceImpl implements ReasonerService {

	@Override
	public String getUnsatisfaisableClasses(String filePath, String Url) throws Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntologyLoaderConfiguration configurator = new OWLOntologyLoaderConfiguration();
	     configurator.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	    // manager.setOntologyConfigurator(configurator);
		
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
	public String postInferencesContent(String ontologyContentDecoded64, List<String> ListOfValues)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
		//set silent imports
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		manager.setOntologyLoaderConfiguration(config);
		OWLOntology ontology = null;
		try {
		    if (instream.available() > 0) {
		        OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
		        ontology = manager.loadOntologyFromOntologyDocument(documentSource);
		    }
		} catch (OWLOntologyCreationException e) {
		    e.printStackTrace();
		    return e.getMessage(); 
		}
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		reasoner.precomputeInferences(InferenceType.values());
		OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators = new ArrayList<>();       
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, axiomGenerators);
		
		//
        for (String value : ListOfValues) {
            try {
                boolean generatorAdded = false;
                if (value.contentEquals("CustomInferredIntersectionOfAxiomGenerator()")   && !generatorAdded ) {
                	iog.addGenerator( new CustomInferredIntersectionOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredEquivalentClassesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredEquivalentClassesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomSameIndividualAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomSameIndividualAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredUnionOfAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredUnionOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDisjointClassesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredDisjointClassesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDifferentIndividualAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredDifferentIndividualAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredHasValueAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredHasValueAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredInverseObjectPropertiesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredInverseObjectPropertiesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredAllValuesFromAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredAllValuesFromAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredSameValueSomeValuesFromAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDomainAndRangeAxiomGenerator()") && !generatorAdded)  {
                	iog.addGenerator( new CustomInferredDomainAndRangeAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredClassAssertionAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredClassAssertionAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredSubClassAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubClassAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredDataPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredEquivalentDataPropertiesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                	generatorAdded = true;
                
                }else if (value.contentEquals("InferredEquivalentObjectPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredSubObjectPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                	generatorAdded = true;
                
                }else if (value.contentEquals("InferredSubDataPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredObjectPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                	generatorAdded = true;;
                }else if (value.contentEquals("InferredPropertyAssertionGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredPropertyAssertionGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredComplementOfAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredComplementOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("All")) {
        	        iog.addGenerator(new CustomInferredEquivalentClassesAxiomGenerator() );
        	        iog.addGenerator(new CustomSameIndividualAxiomGenerator());
        	        iog.addGenerator(new CustomInferredDifferentIndividualAxiomGenerator());
        	        iog.addGenerator(new CustomInferredIntersectionOfAxiomGenerator());
        	        iog.addGenerator(new CustomInferredUnionOfAxiomGenerator());
        	        iog.addGenerator(new CustomInferredDisjointClassesAxiomGenerator());
        	        iog.addGenerator(new CustomInferredHasValueAxiomGenerator());
        	        iog.addGenerator(new CustomInferredInverseObjectPropertiesAxiomGenerator() );
        	        iog.addGenerator(new CustomInferredAllValuesFromAxiomGenerator());
        	        iog.addGenerator(new CustomInferredSameValueSomeValuesFromAxiomGenerator());
        	        iog.addGenerator(new CustomInferredDomainAndRangeAxiomGenerator());
        	        iog.addGenerator( new InferredSubClassAxiomGenerator());
        	        iog.addGenerator( new InferredClassAssertionAxiomGenerator());
        	        iog.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
        	        iog.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
        	        iog.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredSubDataPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
        	        iog.addGenerator( new InferredPropertyAssertionGenerator());
        	        iog.addGenerator(new CustomInferredComplementOfAxiomGenerator()); // Generate owl:ComplementOf
        	        
                        break;
                }else {
                	break;
                }
                if (generatorAdded) {
                    continue;
                }


                
            } catch (Exception e) {
                e.printStackTrace(); // Handle any exceptions that may occur
            }
        }
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		iog.fillOntology(dataFactory, inferredOntology);
		JSONObject jsonObject = new JSONObject();
		for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
			Set<? extends OWLAxiom> axioms = inferredOntology.getAxioms(axiomType);
			if (!axioms.isEmpty()) {
				jsonObject.put(axiomType.toString(), convertAxiomSetToJSONArray(axioms));
			}
		}
		String jsonString = jsonObject.toString();
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
		InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
		//set silent imports
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		manager.setOntologyLoaderConfiguration(config);
		OWLOntology ontology = null;
		try {
		    if (instream.available() > 0) {
		        OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
		        ontology = manager.loadOntologyFromOntologyDocument(documentSource);
		    }
		} catch (OWLOntologyCreationException e) {
		    e.printStackTrace();
		    return e.getMessage(); 
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
	public String postConsistencyContent(String ontologyContentDecoded64)
			throws OWLOntologyCreationException, JsonProcessingException, Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		InputStream instream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
		//set silent imports
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		manager.setOntologyLoaderConfiguration(config);
		OWLOntology ontology = null;
		try {
		    if (instream.available() > 0) {
		        OWLOntologyDocumentSource documentSource = new StreamDocumentSource(instream);
		        ontology = manager.loadOntologyFromOntologyDocument(documentSource);
		    }
		} catch (OWLOntologyCreationException e) {
		    e.printStackTrace();
		    return e.getMessage(); 
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
	public String postInferences(String filePath, String url,List<String> ListOfValues)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
		    if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
		        ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
		    } else if (filePath.isEmpty() == false && url == null) {
		        ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		    }
		} catch (OWLOntologyCreationException e) {
		    e.printStackTrace();
		    return e.getMessage();
		}
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		reasoner.precomputeInferences(InferenceType.values());
		OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators = new ArrayList<>();       
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, axiomGenerators);
        for (String value : ListOfValues) {
            try {
                boolean generatorAdded = false;
                if (value.contentEquals("CustomInferredIntersectionOfAxiomGenerator()")   && !generatorAdded ) {
                	iog.addGenerator( new CustomInferredIntersectionOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredEquivalentClassesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredEquivalentClassesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomSameIndividualAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomSameIndividualAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredUnionOfAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredUnionOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDisjointClassesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredDisjointClassesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDifferentIndividualAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredDifferentIndividualAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredHasValueAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredHasValueAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredInverseObjectPropertiesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredInverseObjectPropertiesAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredAllValuesFromAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredAllValuesFromAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredSameValueSomeValuesFromAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredSameValueSomeValuesFromAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredDomainAndRangeAxiomGenerator()") && !generatorAdded)  {
                	iog.addGenerator( new CustomInferredDomainAndRangeAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredClassAssertionAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredClassAssertionAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredSubClassAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubClassAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredDataPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredEquivalentDataPropertiesAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
                	generatorAdded = true;
                
                }else if (value.contentEquals("InferredEquivalentObjectPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredSubObjectPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
                	generatorAdded = true;
                
                }else if (value.contentEquals("InferredSubDataPropertyAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredSubDataPropertyAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("InferredObjectPropertyCharacteristicAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
                	generatorAdded = true;;
                }else if (value.contentEquals("InferredPropertyAssertionGenerator()") && !generatorAdded) {
                	iog.addGenerator( new InferredPropertyAssertionGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("CustomInferredComplementOfAxiomGenerator()") && !generatorAdded) {
                	iog.addGenerator( new CustomInferredComplementOfAxiomGenerator());
                	generatorAdded = true;
                }else if (value.contentEquals("All")) {
        	        iog.addGenerator(new CustomInferredEquivalentClassesAxiomGenerator() );
        	        iog.addGenerator(new CustomSameIndividualAxiomGenerator()); // we generate same individual axioms
        	        iog.addGenerator(new CustomInferredDifferentIndividualAxiomGenerator()); // we generate different individual axioms
        	        iog.addGenerator(new CustomInferredIntersectionOfAxiomGenerator());
        	        iog.addGenerator(new CustomInferredUnionOfAxiomGenerator());
        	        iog.addGenerator(new CustomInferredDisjointClassesAxiomGenerator());
        	        iog.addGenerator(new CustomInferredHasValueAxiomGenerator());
        	        iog.addGenerator(new CustomInferredInverseObjectPropertiesAxiomGenerator() );
        	        iog.addGenerator(new CustomInferredAllValuesFromAxiomGenerator());
        	        iog.addGenerator(new CustomInferredSameValueSomeValuesFromAxiomGenerator());
        	        iog.addGenerator(new CustomInferredDomainAndRangeAxiomGenerator());
        	        iog.addGenerator( new InferredSubClassAxiomGenerator());
        	        iog.addGenerator( new InferredClassAssertionAxiomGenerator());
        	        iog.addGenerator( new InferredDataPropertyCharacteristicAxiomGenerator());
        	        iog.addGenerator( new InferredEquivalentDataPropertiesAxiomGenerator());
        	        iog.addGenerator( new InferredEquivalentObjectPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredSubObjectPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredSubDataPropertyAxiomGenerator());
        	        iog.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator());
        	        iog.addGenerator( new InferredPropertyAssertionGenerator());
        	        iog.addGenerator(new CustomInferredComplementOfAxiomGenerator()); // Generate owl:ComplementOf
                        break;
                }else {
                	break;
                }
                if (generatorAdded) {
                    continue;
                }


                
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		iog.fillOntology(dataFactory, inferredOntology);
		JSONObject jsonObject = new JSONObject();
		for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
			Set<? extends OWLAxiom> axioms = inferredOntology.getAxioms(axiomType);
			if (!axioms.isEmpty()) {
				jsonObject.put(axiomType.toString(), convertAxiomSetToJSONArray(axioms));
			}
		}
		String jsonString = jsonObject.toString();
		return jsonString;
	}
	@Override
	public String postUnsatisfaisableClasses(String filePath, String Url) throws Exception {
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
	public String postConsistency(String filePath, String Url)
			throws OWLOntologyCreationException, JsonProcessingException, Exception {
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
	public String getConsistency(String filePath, String Url)
			throws OWLOntologyCreationException, JsonProcessingException, Exception {
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
	public String getInferences(String filePath, String url)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
		    if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
		        ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
		    } else if (filePath.isEmpty() == false && url == null) {
		        ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		    }
		} catch (OWLOntologyCreationException e) {
		    e.printStackTrace();
		    return e.getMessage();
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
			try (OutputStream outputStream = ((WritableResource) resource).getOutputStream()) {
				manager.saveOntology(inferredOntology, IRI.create(resource.getURI()));
				System.out.println("New file created: " + fileName);
			} catch (IOException e) {
				System.out.println("An error occurred: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
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

	public static class CustomSameIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLSameIndividualAxiom> {

		@Override
		protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLSameIndividualAxiom> resultAxiom) {
			for (OWLNamedIndividual x : reasoner.getSameIndividuals(entity).getEntities()) {
				if (!entity.equals(x)) {
					resultAxiom.add(dataFactory.getOWLSameIndividualAxiom(entity, x));
				}
			}
		}

		@Override
		public String getLabel() {
			return "OWL:SameAs Inferences";
		}
	}

	public class CustomInferredEquivalentClassesAxiomGenerator
			extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		@Override
		protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLEquivalentClassesAxiom> resultAxiom) {
			Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(entity).getEntities();
			if (equivalentClasses.size() > 1) {
				resultAxiom.add(dataFactory.getOWLEquivalentClassesAxiom(equivalentClasses));
			}
		}

		@Override
		public String getLabel() {
			return "owl:equivalentClass Inferences";
		}
	}

	public class CustomInferredDifferentIndividualAxiomGenerator
			extends InferredIndividualAxiomGenerator<OWLDifferentIndividualsAxiom> {

		@Override
		protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLDifferentIndividualsAxiom> AxiomResult) {
			Set<OWLNamedIndividual> differentOWLNamedIndividual = reasoner.getDifferentIndividuals(entity).getFlattened();
			if (!differentOWLNamedIndividual.isEmpty()) {
				AxiomResult.add(dataFactory.getOWLDifferentIndividualsAxiom(
						Stream.concat(Stream.of(entity), differentOWLNamedIndividual.stream()).toArray(OWLIndividual[]::new)));

			}
		}

		@Override
		public String getLabel() {
			return "Different individuals";
		}
	}

	public class CustomInferredIntersectionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		@Override
		protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLEquivalentClassesAxiom> resultAxiom) {
			NodeSet<OWLClass> SuperClasses = reasoner.getSuperClasses(entity, true);
			if (SuperClasses.isEmpty()) {
				return;
			}

			Set<OWLClassExpression> operands = new HashSet<>();
			for (Node<OWLClass> superClassNode : SuperClasses.getNodes()) {
				operands.add(superClassNode.getRepresentativeElement());
			}

			if (operands.size() > 1) {
				OWLObjectIntersectionOf intersectionOf = dataFactory.getOWLObjectIntersectionOf(operands);
				OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(entity, intersectionOf);
				resultAxiom.add(axiom);
			}
		}

		@Override
		public String getLabel() {
			return "OWL:IntersectionOf Inference";
		}
	}

	public class CustomInferredUnionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {
		@Override
		protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLSubClassOfAxiom> resultAxiom) {
			Set<OWLClass> SuperClasses = reasoner.getSuperClasses(entity, true).getFlattened();
			if (SuperClasses.size() > 1) {
				resultAxiom.add(
						dataFactory.getOWLSubClassOfAxiom(entity, dataFactory.getOWLObjectUnionOf(SuperClasses)));
			}
		}

		@Override
		public String getLabel() {
			return "OWL:UnionOf Inference";
		}
	}

	public class CustomInferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

		@Override
		protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLDisjointClassesAxiom> resultAxiom) {
			Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();
			for (OWLClass classe : allClasses) {
				if (!classe.equals(entity)) {
					NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(entity);
					if (disjointClasses.containsEntity(classe)) {

						Set<OWLClass> equivalentClasses1 = reasoner.getEquivalentClasses(entity).getEntities();
						Set<OWLClass> equivalentClasses2 = reasoner.getEquivalentClasses(classe).getEntities();
						for (OWLClass loopEquivalentClass1 : equivalentClasses1) {
							for (OWLClass loopEquivalentClass2 : equivalentClasses2) {
								if (!loopEquivalentClass1.equals(loopEquivalentClass2)) {
									resultAxiom.add(dataFactory.getOWLDisjointClassesAxiom(loopEquivalentClass1, loopEquivalentClass2));
								}
							}
						}
					}
				}
			}
		}

		@Override
		public String getLabel() {
			return "OWL DisjointClasses";
		}
	}

	public class InferredSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		@Override
		protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLSubClassOfAxiom> result) {
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
			return Collections.emptySet();
		}

		@Override
		public String getLabel() {
			return "Some values from";
		}
	}

	public class CustomInferredHasValueAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		@Override
		protected void addAxioms(OWLClass cls, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLSubClassOfAxiom> resultAxiom) {
			for (OWLClassExpression equivalentClass : reasoner.getEquivalentClasses(cls).getEntities()) {
				if (equivalentClass.isAnonymous() && equivalentClass instanceof OWLObjectHasValue) {
					OWLObjectHasValue owlHasValue = (OWLObjectHasValue) equivalentClass;
					OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, owlHasValue);
					resultAxiom.add(axiom);
				}
			}
		}

		public Set<InferredAxiomGenerator<?>> getInferredAxiomGenerators() {
			return Collections.emptySet();
		}

		@Override
		public String getLabel() {
			return "Owl:hasValue Inferences";
		}
	}

	public class CustomInferredInverseObjectPropertiesAxiomGenerator
			implements InferredAxiomGenerator<OWLInverseObjectPropertiesAxiom> {

		@Override
		public Set<OWLInverseObjectPropertiesAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
			Set<OWLInverseObjectPropertiesAxiom> resultSet = new HashSet<>();
			for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
				addAxioms(objectProperty, reasoner, dataFactory, resultSet);
			}
			return resultSet;
		}

		protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLInverseObjectPropertiesAxiom> resultAxiom) {
			for (OWLObjectPropertyExpression inverseAxiom : reasoner.getInverseObjectProperties(entity).getEntities()) {
				if (!inverseAxiom.equals(entity)) {
					resultAxiom.add(dataFactory.getOWLInverseObjectPropertiesAxiom(entity, inverseAxiom));
				}
			}
		}

		@Override
		public String getLabel() {
			return "owl:InverseFunctionalProperty";
		}
	}

	public class CustomInferredAllValuesFromAxiomGenerator implements InferredAxiomGenerator<OWLSubClassOfAxiom> {

		@Override
		public Set<OWLSubClassOfAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
			Set<OWLSubClassOfAxiom> resultSet = new HashSet<>();
			for (OWLClass owlClass : reasoner.getRootOntology().getClassesInSignature()) {
				addAxioms(owlClass, reasoner, dataFactory, resultSet);
			}
			return resultSet;
		}

		protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
				Set<OWLSubClassOfAxiom> resultAxiom) {
			for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
				NodeSet<OWLClass> possibleRanges = reasoner.getObjectPropertyRanges(objectProperty, true);
				if (!possibleRanges.isEmpty()) {
					OWLClassExpression owlAllValuesFrom = dataFactory.getOWLObjectAllValuesFrom(objectProperty,
							possibleRanges.getFlattened().iterator().next());
					OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entity, owlAllValuesFrom);
					resultAxiom.add(axiom);
				}
			}
		}

		@Override
		public String getLabel() {
			return "owl:allValuesFrom ";
		}
	}

	public class CustomInferredSameValueSomeValuesFromAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

		@Override
		protected void addAxioms(OWLClass entty, OWLReasoner reasoner, OWLDataFactory dataFactory,
		        Set<OWLSubClassOfAxiom> resultAxioms) {
		    for (OWLObjectProperty objectProperty : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
		        NodeSet<OWLClass> possibleRanges = reasoner.getObjectPropertyRanges(objectProperty, true);
		        if (!possibleRanges.isEmpty()) {
		            OWLClassExpression owlSomeValuesFromRslt = dataFactory.getOWLObjectSomeValuesFrom(objectProperty,
		                    possibleRanges.getFlattened().iterator().next());
		            OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(entty, owlSomeValuesFromRslt);
		            resultAxioms.add(axiom);
		        }
		    }
		}

		@Override
		public String getLabel() {
			return "owl:someValueFrom";
		}
	}

	public class CustomInferredDomainAndRangeAxiomGenerator implements InferredAxiomGenerator<OWLObjectPropertyAxiom> {

		@Override
		public Set<OWLObjectPropertyAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
			Set<OWLObjectPropertyAxiom> resultSet = new HashSet<>();
			// Two loops to get inference from domain and range
			for (OWLObjectProperty property : reasoner.getRootOntology().getObjectPropertiesInSignature()) {
				for (OWLClass domainInf : reasoner.getObjectPropertyDomains(property, true).getFlattened()) {
					resultSet.add(dataFactory.getOWLObjectPropertyDomainAxiom(property, domainInf));
				}
				for (OWLClass rangeInf : reasoner.getObjectPropertyRanges(property, true).getFlattened()) {
					resultSet.add(dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeInf));
				}
			}

			return resultSet;
		}

		@Override
		public String getLabel() {
			return "Inference of Domain And Range";
		}
	}
	
	public class CustomInferredComplementOfAxiomGenerator implements InferredAxiomGenerator<OWLClassAxiom> {

	    @Override
	    public Set<OWLClassAxiom> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
	        Set<OWLClassAxiom> resultSet = new HashSet<>();

	        Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();

	        for (OWLClass cls : allClasses) {
	            if (!reasoner.isSatisfiable(cls) && !cls.isOWLNothing()) {
	                NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(cls);
	                disjointClasses.getFlattened().forEach(disjointClass -> {
	                    OWLObjectComplementOf ObjectComplementOf = dataFactory.getOWLObjectComplementOf(disjointClass);
	                    OWLEquivalentClassesAxiom axiomEQ = dataFactory.getOWLEquivalentClassesAxiom(cls, ObjectComplementOf);
	                    resultSet.add(axiomEQ);
	                });
	            }
	        }

	        return resultSet;
	    }

	    @Override
	    public String getLabel() {
	        return "complementOf";
	    }
	}

	public class CustomInferredOntologyGenerator extends InferredOntologyGenerator {

	    public CustomInferredOntologyGenerator(OWLReasoner reasoner) {
	        super(reasoner);
	    }
	}
	
}
