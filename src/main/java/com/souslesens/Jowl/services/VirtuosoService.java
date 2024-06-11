package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.Ontology;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface VirtuosoService {

    JSONObject querySparql(String query) throws IOException, URISyntaxException, MalformedChallengeException, AuthenticationException;

    Ontology readOntologyFromVirtuoso(String graphName) throws OWLOntologyCreationException, NoVirtuosoTriplesException;

    JSONArray getTriplesVirtuosoSparql(String graphName) throws MalformedChallengeException, URISyntaxException, IOException, AuthenticationException;

}
