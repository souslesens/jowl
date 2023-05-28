package com.souslesens.Jowl.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
@Service

public class SWRLServiceImpl implements SWRLService {


	@Override
	public String SWRLruleMeth1(String filePath, String Url) throws Exception {
		return null;
	}
	// TEST

	@Override
	public String SWRLruleMeth2(String ontologyContentDecoded64)
			throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {

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
		
		// RULE : Person (x) ^ Human (x) -> Student(x)
		// Get data factory
		OWLDataFactory factory = manager.getOWLDataFactory();

		// Create classes
		OWLClass classPerson = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#Person"));
		OWLClass classHuman = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#Human"));
		OWLClass classStudent = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#Student"));

		// Create SWRL Variable
		SWRLVariable var = factory.getSWRLVariable(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#x"));

		// Create SWRL rule
		SWRLClassAtom body1 = factory.getSWRLClassAtom(classPerson, var);
		SWRLClassAtom body2 = factory.getSWRLClassAtom(classHuman, var);
		SWRLClassAtom head = factory.getSWRLClassAtom(classStudent, var);
//		SWRLRule rule = factory.getSWRLRule(Collections.singleton(body), Collections.singleton(head));

		Set<SWRLAtom> body = new HashSet<>();
		body.add(body1);
		body.add(body2);
		
		SWRLRule rule = factory.getSWRLRule(body, Collections.singleton(head));
		AddAxiom addAxiom = new AddAxiom(ontology, rule);
		// Add SWRL rule to ontology
		manager.applyChange(addAxiom);
//		manager.applyChange(new AddAxiom(ontology, rule));
		

		
		// END OF //
		// Creation reasoner 
		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//
		// Computing inferences
		reasoner.precomputeInferences(InferenceType.values());
		//
		//
		NodeSet<OWLNamedIndividual> inferredStudents = reasoner.getInstances(classStudent, false); // false = only inferred
		//
		for (OWLNamedIndividual student : inferredStudents.getFlattened()) {
		    System.out.println("Inferred student: " + student.getIRI());
		}
		//
		OWLOntology inferredOntology = manager.createOntology();    
        List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators = new ArrayList<>();       
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, axiomGenerators);
		
		//
//		// we write what is inferred for amine
//		OWLNamedIndividual Amine = factory.getOWLNamedIndividual(IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#Amine"));
//		NodeSet<OWLClass> inferredClasses = reasoner.getTypes(Amine, true);
//		for (OWLClass inferredClass : inferredClasses.getFlattened()) {
//		    System.out.println("Amine belongs to: " + inferredClass);
//		}
//		//
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



}
