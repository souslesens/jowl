package com.souslesens.Jowl.services;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface ReasonerService {
    String getUnsatisfaisableClasses(String filePath, String operation) throws Exception;
    String getConsistency(String filePath, String operation) throws OWLOntologyCreationException, JsonProcessingException, Exception;
}
