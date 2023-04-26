package com.souslesens.Jowl.services;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerExtractTriples;


public interface AlternativeReasonerService {
	
	
    
	//Alternative
    String getUnsatisfaisableClassesAlt(String filePath, String Url,MultipartFile ontologyFile) throws Exception;
    String getConsistencyAlt(String filePath, String Url,MultipartFile ontologyFile) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    List<reasonerExtractTriples> getInferencesAlt(String filePath, String Url,MultipartFile ontologyFile) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
	
    
    
}
