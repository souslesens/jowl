package com.souslesens.Jowl.services;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerExtractTriples;


public interface ReasonerService {
    String getUnsatisfaisableClasses(String filePath, String operation) throws Exception;
    String getConsistency(String filePath, String operation) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    List<reasonerExtractTriples> getInferences(String filePath, String operation) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
}
