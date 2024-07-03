package com.souslesens.Jowl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

public interface HermitReasonerService {

    String getUnsatisfaisableClasses(String filePath, String Url) throws Exception;

    String getUnsatisfaisableClasses(String ontologyContentDecoded64) throws Exception;

    String getConsistency(String filePath, String Url) throws Exception;

    String getConsistency(String ontologyContentDecoded64)
            throws Exception;

    boolean getConsistency(OWLOntology ontology);

    String getInferences(String filePath, String Url) throws Exception;

    String getInferences(String ontologyContentDecoded64)
            throws Exception;

}
