package com.souslesens.Jowl.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.gson.Gson;
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

}
