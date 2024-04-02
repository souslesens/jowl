package com.souslesens.Jowl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

public interface HermitReasonerService {

    String getUnsatisfaisableClasses(String filePath, String Url) throws Exception;
    String getConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException, Exception;
    String getInferences(String filePath, String Url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;

}
