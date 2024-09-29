package com.souslesens.Jowl.services;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Map;

public interface HermitReasonerService {

    String getUnsatisfaisableClasses(String filePath, String Url, String grapheName) throws Exception;

    String getUnsatisfaisableClasses(String ontologyContentDecoded64) throws Exception;

    String getConsistency(String filePath, String Url, String graphName) throws Exception;

    String getConsistency(String ontologyContentDecoded64)
            throws Exception;

    boolean getConsistency(OWLOntology ontology);

    String getInferences(String filePath, String Url, List<String> ListOfValues, String graphName) throws Exception;

    String getInferences(String ontologyContentDecoded64, List<String> ListOfValues)
            throws Exception;

    public Map<String, String> getParameteresInferenceMethod();


}
