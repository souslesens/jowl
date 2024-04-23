package com.souslesens.Jowl.services;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public interface VirtuosoService {

    OWLOntology readOntologyFromVirtuoso(String graphName) throws OWLOntologyCreationException, OWLOntologyStorageException;

}
