package com.souslesens.Jowl.services;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerInference;


public interface ReasonerService {
	
	// GET API METHODS
    String getUnsatisfaisableClasses(String filePath, String Url) throws Exception;
    String getConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    String getInferences(String filePath, String Url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
	// POST API METHODS
	String postConsistency(String filePath, String url, String ontologyContentDecoded64) throws OWLOntologyCreationException, JsonProcessingException, Exception;
	String postInferences(String filePath, String Url, String ontologyContentDecoded64) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
	String postUnsatisfaisableClasses(String filePath, String Url, String ontologyContentDecoded64) throws Exception;
    
    
}
