package com.souslesens.Jowl.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerExtractTriples;


public interface ReasonerService {
    String getUnsatisfaisableClasses(String filePath, String Url) throws Exception;
    String getConsistency(String filePath, String Url,MultipartFile ontologyFile) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    List<reasonerExtractTriples> getInferences(String filePath, String Url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
}
