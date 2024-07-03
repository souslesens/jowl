package com.souslesens.Jowl.services;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface ReasonerService {
	
	// GET API METHODS
    String getUnsatisfaisableClasses(String filePath, String Url) throws Exception;
    String getConsistency(String filePath, String Url) throws Exception;
    String getInferences(String filePath, String Url) throws Exception;
	// POST API METHODS
	
	String postUnsatisfaisableClassesContent(String ontologyContentDecoded64) throws Exception;
	
	String postInferencesContent(String ontologyContentDecoded64, List<String> valuesList)
			throws Exception;
	
	String postConsistencyContent(String ontologyContentDecoded64)
			throws Exception;

	String postInferences(String filePath, String url , List<String> valuesList)
			throws Exception;
	
	String postConsistency(String filePath, String Url)
			throws Exception;
	
	String postUnsatisfaisableClasses( String filePath, String Url) throws Exception;
    
    
}
