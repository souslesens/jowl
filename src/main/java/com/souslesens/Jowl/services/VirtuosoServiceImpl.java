package com.souslesens.Jowl.services;

import java.io.BufferedReader;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.souslesens.Jowl.config.AppConfig;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.jena.rdf.model.Resource;
import org.jpos.util.NameRegistrar;
import org.apache.http.message.BasicHttpRequest;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.AnonId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.impl.auth.DigestScheme;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import java.net.URLEncoder;

@Service
public class VirtuosoServiceImpl implements VirtuosoService {

    @Autowired
    private AppConfig appConfig;

    @Override
    public OWLOntology readOntologyFromVirtuoso(String graphName) throws OWLOntologyCreationException, NoVirtuosoTriplesException {

        JSONArray triples = null;

        try {
            System.out.println("querying sparql");
            triples = getTriplesVirtuosoSparql(graphName);
            if (triples == null || triples.length() == 0)
                throw new NoVirtuosoTriplesException("No triples found in the graph " + graphName);
        } catch (IOException | URISyntaxException | MalformedChallengeException | AuthenticationException e) {
            throw new RuntimeException(e);
        }

        OntologyManager m = OntManagers.createManager();
        Ontology o = m.createOntology(IRI.create(graphName));

        for (int i = 0; i < triples.length(); i++) {
            JSONObject triple = triples.getJSONObject(i);
            String subjectValue = triple.getJSONObject("subject").getString("value");
            String predicate = triple.getJSONObject("predicate").getString("value");
            String objectValue = triple.getJSONObject("object").getString("value");

            String subjectType = triple.getJSONObject("subject").getString("type");
            String objectType = triple.getJSONObject("object").getString("type");

            Resource subject;
            if (subjectType.equals("uri")) {
                subject = ResourceFactory.createResource(subjectValue);
            } else if (subjectType.equals("bnode") || subjectValue.startsWith("nodeID://")) {
                subject = o.asGraphModel().createResource(new AnonId(subjectValue.replace("nodeID://", "")));
            } else {
                throw new IllegalArgumentException("Unexpected subject type: " + subjectType);
            }

            Resource object;
            if (objectType.equals("uri")) {
                object = ResourceFactory.createResource(objectValue);
            } else if (objectType.equals("bnode") || objectValue.startsWith("nodeID://")) {
                object = o.asGraphModel().createResource(new AnonId(objectValue.replace("nodeID://", "")));
            } else {
                throw new IllegalArgumentException("Unexpected object type: " + objectType);
            }

            if (predicate.equals("http://www.w3.org/2002/07/owl#imports")) {
                if (object.isResource()) {
                    Ontology importedOntology = m.loadOntology(IRI.create((object).getURI()));
                    o.asGraphModel().add(importedOntology.asGraphModel());
                }
            }

            o.asGraphModel().add(ResourceFactory.createStatement(
                    subject,
                    ResourceFactory.createProperty(predicate),
                    object
            ));

        }

        if (o.axioms().findAny().isPresent()) {
            System.out.println(o.axioms().count());
            return o;
        }

        System.out.println("Error reading ontology from Virtuoso");
        return null;
    }


    public JSONArray getTriplesVirtuosoSparql(String graphName) throws MalformedChallengeException, URISyntaxException, IOException, AuthenticationException {

        final DigestScheme md5Auth = new DigestScheme();
        String query = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + graphName + "> { ?subject ?predicate ?object . FILTER(isIRI(?object) || isBlank(?object)) } }";
        //String query = "SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object} LIMIT 10";
        URI endpoint = new URI(appConfig.getVirtuosoEndpoint() + "?format=json&query=" + URLEncoder.encode(query, "UTF-8"));
        HttpGet request = new HttpGet(endpoint);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse authResponse = client.execute(request);


        if (authResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            BufferedReader rd = new BufferedReader(new InputStreamReader(authResponse.getEntity().getContent()));
            StringBuffer result = new StringBuffer();

            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return new JSONObject(result.toString()).getJSONObject("results").getJSONArray("bindings");

        } else if (authResponse.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            if (authResponse.containsHeader("WWW-Authenticate")) {
                final Header challenge = authResponse.getHeaders("WWW-Authenticate")[0];
                NameRegistrar.register("www-authenticate", authResponse.getHeaders("WWW-Authenticate")[0]);
                md5Auth.processChallenge(challenge);

                System.out.println(appConfig.getVirtuosoUser() + appConfig.getVirtuosoPassword());
                final Header solution = md5Auth.authenticate(
                        new UsernamePasswordCredentials(appConfig.getVirtuosoUser(), appConfig.getVirtuosoPassword()),
                        new BasicHttpRequest(HttpGet.METHOD_NAME, endpoint
                                .getPath()), new HttpClientContext());

                md5Auth.createCnonce();
                request.addHeader(solution.getName(), solution.getValue());
                HttpResponse response = client.execute(request);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();

                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                return new JSONObject(result.toString()).getJSONObject("results").getJSONArray("bindings");

            } else {
                throw new Error("Web-service responded with Http 401, " +
                        "but didn't send us a usable WWW-Authenticate header.");
            }
        } else {
            throw new Error("Didn't get an Http 401 " +
                    "like we were expecting.");
        }
    }

}

