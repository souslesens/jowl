package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.model.jenaTripleParser;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public interface ManchesterService {

    public OWLAxiom parseStringToAxiom(String graphName, String input) throws OWLOntologyCreationException, ParsingAxiomException, NoVirtuosoTriplesException;

    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom);

    public boolean checkManchesterAxiomConsistency(String graphName, OWLAxiom axiom) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    public String triplesToManchester(String graphName, jenaTripleParser[] triples) throws OWLOntologyCreationException, NoVirtuosoTriplesException, AuthenticationException, MalformedChallengeException, IOException, URISyntaxException;

}
