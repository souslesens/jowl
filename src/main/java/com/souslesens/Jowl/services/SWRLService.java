package com.souslesens.Jowl.services;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.souslesens.Jowl.model.SWRLTypeEntityVariable;

public interface SWRLService {

	
	String SWRLruleReclassification(String filePath, String Url, String[] reqBodies , String[] reqHead) throws Exception;
	String SWRLruleReclassificationB64(String ontologyContentDecoded64 , String[] reqBodies , String[] reqHead) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
	String SWRLruleVAB64(String ontologyContentDecoded64 , List<SWRLTypeEntityVariable> reqBodies , List<SWRLTypeEntityVariable> reqHead) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception;
}
