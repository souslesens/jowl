package com.souslesens.Jowl.services;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.fasterxml.jackson.core.JsonProcessingException;


public interface ReasonerService {
    String getUnsatisfaisableClasses(String filePath, String operation) throws Exception;
    String getConsistency(String filePath, String operation) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    String getInferences(String filePath, String operation) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
}
