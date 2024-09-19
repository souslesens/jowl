package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.model.jenaTripleParser;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public interface ManchesterService {

    OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException, ParsingAxiomException, NoVirtuosoTriplesException;

    ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom);

    boolean checkManchesterAxiomConsistency(String graphName, OWLAxiom axiom) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    String triplesToManchester(String graphName, jenaTripleParser[] triples) throws OWLOntologyCreationException, NoVirtuosoTriplesException, AuthenticationException, MalformedChallengeException, IOException, URISyntaxException, OWLOntologyStorageException;

    String triplesToManchester(String axiomGraphName) throws AuthenticationException, MalformedChallengeException, IOException, URISyntaxException;

    String getClassAxioms(String graphName, String classUri, String axiomType, boolean manchesterFormat, boolean triplesFormat) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    boolean checkTriplesConsistency(String graphName, ArrayList<jenaTripleParser> triples, boolean saveTriples) throws OWLOntologyCreationException, NoVirtuosoTriplesException, AuthenticationException, MalformedChallengeException, IOException, URISyntaxException;

}
