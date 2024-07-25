package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.Ontology;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassesServiceImpl implements ClassesService{

    @Autowired
    VirtuosoService virtuosoService;
    @Override
    public JSONArray listClassesWithAxioms(String graphName) throws OWLOntologyCreationException, NoVirtuosoTriplesException {

        OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
        Ontology owlOntology = virtuosoService.readOntologyFromVirtuoso(graphName);

        JSONArray result  = new JSONArray();

        for (OWLClass owlClass : owlOntology.getClassesInSignature()) {
            JSONObject classObject = new JSONObject();
            classObject.put("class", owlClass.getIRI().toString());

            // Fetching class label if available
            String classLabel = owlClass.getIRI().getShortForm(); // Simplified: retrieve actual label from ontology if needed
            classObject.put("label", classLabel);

            Set<String> axiomTypesSet = new HashSet<>();
            Set<OWLClassAxiom> axioms = owlOntology.getAxioms(owlClass);
            for (OWLClassAxiom axiom : axioms) {
                axiomTypesSet.add(axiom.getAxiomType().getName());
            }
            JSONArray axiomsArray = new JSONArray(axiomTypesSet);
            classObject.put("axiomTypes", axiomsArray);
            result.put(classObject);
        }

        return result;

    }

    private String getClassLabel(OWLClass owlClass, OWLOntology owlOntology) {
        for (OWLAnnotation annotation : owlOntology.getAnnotations()) {
            if (annotation.getProperty().isLabel()) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    List<OWLClass> classesInSignature = annotation.classesInSignature().toList();
                    if (classesInSignature.contains(owlClass)) {
                        return val.getLiteral();
                    }
                }
            }
        }
        return owlClass.getIRI().getShortForm(); // Fallback to short form
    }
}
