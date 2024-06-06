package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.model.jenaTripleParser;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.ArrayList;

public interface ManchesterService {

    public OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException, ParsingAxiomException, NoVirtuosoTriplesException;

    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom);

    public boolean checkManchesterAxiomConsistency(String graphName, OWLAxiom axiom) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

}
