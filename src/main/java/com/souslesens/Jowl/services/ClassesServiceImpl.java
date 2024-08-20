package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.Ontology;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassesServiceImpl implements ClassesService{

    @Autowired
    VirtuosoService virtuosoService;
    @Override
    public JSONArray listClassesWithAxioms(String graphName, String axiomType, boolean complexAxioms) throws OWLOntologyCreationException, NoVirtuosoTriplesException {

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

            boolean hasComplexAxiom = false;

            System.out.println("treating class " + owlClass.getIRI().toString());
            System.out.println("axiom type " + axiomType);


            for (OWLClassAxiom axiom : axioms) {

                String currentAxiomType = axiom.getAxiomType().getName();
                System.out.println("current axiom type " + axiomType);
                System.out.println("axiom " + axiom.toString());
                System.out.println("classes count in axioms " + axiom.getClassesInSignature().size());
                System.out.println("complex " + isComplex(axiom));
                if (axiomType == null || axiomType.isEmpty() || axiomType.equalsIgnoreCase(currentAxiomType)) {

                    // Check for complexity
                    if (complexAxioms && !hasComplexAxiom) {
                        if (isComplex(axiom)) {
                            axiomTypesSet.add(currentAxiomType);
                            hasComplexAxiom = true;
                        } else {
                            // Skip this class if complexAxioms is true and this axiom is not complex
                            hasComplexAxiom = false;
                        }
                    }
                }
            }
            if ((!complexAxioms || hasComplexAxiom) && !axiomTypesSet.isEmpty()) {
                JSONArray axiomsArray = new JSONArray(axiomTypesSet);
                classObject.put("axiomTypes", axiomsArray);
                result.put(classObject);
            }
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


    public static boolean isComplex(OWLClassAxiom axiom) {
        if (axiom instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
            // Check if the superclass is complex
            return !subClassAxiom.getSuperClass().isOWLClass(); // returns true if the superclass is a complex expression
        } else if (axiom instanceof OWLEquivalentClassesAxiom) {
            OWLEquivalentClassesAxiom equivalentAxiom = (OWLEquivalentClassesAxiom) axiom;
            // Check if there are more than two classes or if any are complex
            return equivalentAxiom.getClassExpressions().stream()
                    .anyMatch(expr -> !expr.isOWLClass()); // returns true if any class expression is complex
        } else if (axiom instanceof OWLDisjointClassesAxiom) {
            OWLDisjointClassesAxiom disjointAxiom = (OWLDisjointClassesAxiom) axiom;
            // Check if there are more than two classes or if any are complex
            return disjointAxiom.getClassExpressions().size() > 2 ||
                    disjointAxiom.getClassExpressions().stream()
                            .anyMatch(expr -> !expr.isOWLClass()); // returns true if any class expression is complex
        }
        return false; // Default to non-complex for unsupported types
    }

}
