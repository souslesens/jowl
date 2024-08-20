package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.json.JSONArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public interface ClassesService {
    JSONArray listClassesWithAxioms(String graphName, String axiomType, boolean complexAxioms) throws OWLOntologyCreationException, NoVirtuosoTriplesException;
}
