package com.souslesens.Jowl.services;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public interface SWRLService {

	
	String SWRLruleReclassification(String filePath, String Url, String[] reqBodies , String[] reqHead) throws Exception;
	String SWRLruleReclassificationB64(String ontologyContentDecoded64 , String[] reqBodies , String[] reqHead) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
}
