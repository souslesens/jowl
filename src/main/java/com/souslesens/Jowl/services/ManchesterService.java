package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.jenaTripleParser;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.ArrayList;

public interface ManchesterService {

    public OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException;

    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom) throws OWLOntologyStorageException;

}
