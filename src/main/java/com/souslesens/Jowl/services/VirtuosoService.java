package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.Ontology;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.jenaTripleParser;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public interface VirtuosoService {

    JSONObject querySparql(String query) throws IOException, URISyntaxException, MalformedChallengeException, AuthenticationException;

    Ontology readOntologyFromVirtuoso(String graphName, boolean bringCreatedAxioms) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    Ontology getOntology(String graphName) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    JSONArray getTriplesVirtuosoSparql(String graphName) throws MalformedChallengeException, URISyntaxException, IOException, AuthenticationException;

    boolean saveTriples(String graphName,String classUri, String axiomType, ArrayList<jenaTripleParser> triples) throws AuthenticationException, MalformedChallengeException, IOException, URISyntaxException, JSONException;

}
