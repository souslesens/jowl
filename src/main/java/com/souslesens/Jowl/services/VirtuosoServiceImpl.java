package com.souslesens.Jowl.services;

import java.io.BufferedReader;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.google.common.collect.Multimaps;
import com.souslesens.Jowl.config.AppConfig;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;

import com.souslesens.Jowl.model.jenaTripleParser;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.apache.http.impl.auth.DigestScheme;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
public class VirtuosoServiceImpl implements VirtuosoService {

    public static void main(String[] args) {

        VirtuosoServiceImpl processor = new VirtuosoServiceImpl();
        try {
            String graphName = "http://purl.obolibrary.org/obo/vo.owl";
            String query = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + graphName + "> { ?subject ?predicate ?object . FILTER(isIRI(?object) || isBlank(?object)) } }";

            JSONObject data = processor.querySparql(query);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        } catch (MalformedChallengeException ex) {
            throw new RuntimeException(ex);
        } catch (AuthenticationException ex) {
            throw new RuntimeException(ex);

        }

    }

    @Autowired
    private AppConfig appConfig;

    @Override
    @Cacheable(value = "ontologyCache", key = "#graphName")
    public Ontology getOntology(String graphName) throws OWLOntologyCreationException, NoVirtuosoTriplesException {
        // Logic to load the ontology from Virtuoso or another source
        return readOntologyFromVirtuoso(graphName, false);
    }

    @Override
    public JSONObject querySparql(String query) throws IOException, URISyntaxException, MalformedChallengeException, AuthenticationException, JSONException {

        String endPoint ="https://sls.kg-alliance.org/virtuoso/sparql"; // "http://51.178.139.80:8890/sparql";
        String login = "sba";
        String password = "sls#209";


        final DigestScheme md5Auth = new DigestScheme();

        int offset = 0;
        int limit = 10000;
        int linesInbatch = 0;
        StringBuffer result = new StringBuffer();
        do {

            String query2 = query + " offset " + offset + " limit " + limit;
            // query += " offset " + offset + " limit " + limit;
            offset += limit;
            // URI endpoint = new URI(appConfig.getVirtuosoEndpoint() + "?format=json&query=" + URLEncoder.encode(query2, StandardCharsets.UTF_8));
            URI endpoint = new URI(endPoint + "?format=json&query=" + URLEncoder.encode(query2, StandardCharsets.UTF_8));

           // System.out.println("----- endpoint " + endpoint);
            HttpGet request = new HttpGet(endpoint);
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse authResponse = client.execute(request);

            String line = "";
            linesInbatch = 0;

            if (authResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(authResponse.getEntity().getContent()));


                while ((line = rd.readLine()) != null) {
                    linesInbatch += 1;
                    result.append(line);
                }


                //    return new JSONObject(result.toString()).getJSONObject("results");

            } else if (authResponse.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                if (authResponse.containsHeader("WWW-Authenticate")) {
                    final Header challenge = authResponse.getHeaders("WWW-Authenticate")[0];
                    NameRegistrar.register("www-authenticate", authResponse.getHeaders("WWW-Authenticate")[0]);
                    md5Auth.processChallenge(challenge);

                    final Header solution = md5Auth.authenticate(
                            //new UsernamePasswordCredentials(appConfig.getVirtuosoUser(), appConfig.getVirtuosoPassword()),
                            new UsernamePasswordCredentials(login, password),

                            new BasicHttpRequest(HttpGet.METHOD_NAME, endpoint
                                    .getPath()), new HttpClientContext());

                    DigestScheme.createCnonce();
                    request.addHeader(solution.getName(), solution.getValue());
                    HttpResponse response = client.execute(request);
                    Object str= response.getEntity().getContent();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    //  StringBuffer result = new StringBuffer();


                    while ((line = rd.readLine()) != null) {

                        linesInbatch += 1;
                        result.append(line);
                    }


                   // System.out.println("----------linesInbatch" + linesInbatch);
                   // System.out.println("----------Result" + result.length());
                    // return new JSONObject(result.toString()).getJSONObject("results");

                } else {
                    throw new Error("Web-service responded with Http 401, " +
                            "but didn't send us a usable WWW-Authenticate header.");
                }
            } else {
                throw new Error("Didn't get an Http 401 " +
                        "like we were expecting, instead we got:" + authResponse.getStatusLine().getStatusCode());
            }
        }
        while (linesInbatch >= limit);

        return new JSONObject(result.toString()).getJSONObject("results");
    }

    @Override
    public Ontology readOntologyFromVirtuoso(String graphName, boolean bringCreatedAxioms) throws OWLOntologyCreationException, NoVirtuosoTriplesException {

        JSONArray triples = new JSONArray();

        try {

            if (bringCreatedAxioms) {
                System.out.println("querying graphnames");
                JSONArray graphNames = getGraphNamesVirtuosoSparql(graphName);

                System.out.println(graphNames);

                for (int i = 0; i < graphNames.length(); i++) {
                    String matchedGraphName = graphNames.getJSONObject(i).getJSONObject("graph").getString("value");
                    System.out.println("Fetching triples from graph: " + matchedGraphName);

                    JSONArray graphTriples = getTriplesVirtuosoSparql(matchedGraphName);
                    if (graphTriples != null && graphTriples.length() > 0) {

                        for (int j = 0; j < graphTriples.length(); j++) {
                            triples.put(graphTriples.get(j));
                        }
                    }
                }
            } else {
                System.out.println("querying sparql");
                triples = getTriplesVirtuosoSparql(graphName);
            }
            if (triples.length() == 0)
                throw new NoVirtuosoTriplesException("No triples found in the graph " + graphName);
        } catch (IOException | URISyntaxException | MalformedChallengeException | AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        OntologyManager m = OntManagers.createManager();
        Ontology o = m.createOntology(IRI.create(graphName));
        System.out.println("---------------triples found" + triples.length());
        for (int i = 0; i < triples.length(); i++) {
            JSONObject triple = triples.getJSONObject(i);
            String subjectValue = triple.getJSONObject("subject").getString("value");
            String predicate = triple.getJSONObject("predicate").getString("value");
            String objectValue = triple.getJSONObject("object").getString("value");

            String subjectType = triple.getJSONObject("subject").getString("type");
            String objectType = triple.getJSONObject("object").getString("type");

            Resource subject;
            if (subjectType.equals("uri") && !subjectValue.startsWith("_")) {
                subject = ResourceFactory.createResource(subjectValue);
            } else if (subjectType.equals("bnode") || subjectValue.startsWith("nodeID://") || subjectValue.startsWith("_")) {
                subject = o.asGraphModel().createResource(new AnonId(subjectValue));
            } else {
                throw new IllegalArgumentException("Unexpected subject type: " + subjectType);
            }

            Resource object;
            if (objectType.equals("uri") && !objectValue.startsWith("_")) {
                object = ResourceFactory.createResource(objectValue);
            } else if (objectType.equals("bnode") || objectValue.startsWith("nodeID://") || objectValue.startsWith("_")) {
                object = o.asGraphModel().createResource(new AnonId(objectValue));
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


    public JSONArray getTriplesVirtuosoSparql(String graphName) throws MalformedChallengeException, URISyntaxException, IOException, AuthenticationException, JSONException {

        String query = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + graphName + "> { ?subject ?predicate ?object . FILTER(isIRI(?object) || isBlank(?object)) } }";
        //String query = "SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object} LIMIT 10";


        JSONArray result = querySparql(query).getJSONArray("bindings");
        System.out.println("------------    result  -------------" + result.length());
        return result;


    }

    @Override
    public boolean saveTriples(String graphName, String classUri, String axiomType, ArrayList<jenaTripleParser> triplesList) throws AuthenticationException, JSONException, MalformedChallengeException, IOException, URISyntaxException {

        ArrayList<jenaTripleParser> triples = new ArrayList<>();
        for (jenaTripleParser triple : triplesList) {
            triples.add(new jenaTripleParser(triple.getSubject(), triple.getPredicate(), triple.getObject()));
        }

//        String newGraphName = "";
//        if (classUri != null && !classUri.isEmpty()) {
//            String uuid = java.util.UUID.randomUUID().toString(); // Generates a unique UUID
//            //getting last part of classUri
//            String[] parts = classUri.split("[#/]");
//            String classLastPart = parts[parts.length - 1];
//
//
//            newGraphName = graphName + "concepts/" + classLastPart + "/" + axiomType + "/" + uuid + "/";
//            //create new graph
//            String createGraphQuery = "CREATE GRAPH <" + newGraphName + ">";
//            querySparql(createGraphQuery);
//            System.out.println("new graph name: " + newGraphName);
//        } else {
//            newGraphName = graphName;
//        }

        //reformulate blank node uris to an acceptable format by triple store
        //triples = replaceBlankNodesWithURIs(triples);

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT DATA { GRAPH <" + graphName + "> { ");

        // Loop through the triples and add them to the query
        for (jenaTripleParser triple : triples) {

            queryBuilder.append("<")
                    .append(triple.getSubject())
                    .append("> ");


            // Handle the predicate (always a URI)
            queryBuilder.append("<")
                    .append(triple.getPredicate())
                    .append("> ");

            queryBuilder.append("<")
                    .append(triple.getObject())
                    .append("> ");


            queryBuilder.append(". ");
        }

        // Close the SPARQL query
        queryBuilder.append("} }");

        // Convert the query to a string
        String query = queryBuilder.toString();

        System.out.println(query);

        System.out.println(querySparql(query));

        return true;


    }

    private boolean isBlankNode(String value) {
        return !value.startsWith("http");
    }


    public ArrayList<jenaTripleParser> replaceBlankNodesWithURIs(ArrayList<jenaTripleParser> triples) {
        for (jenaTripleParser triple : triples) {
            if (isBlankNode(triple.getSubject())) {
                triple.setSubject("urn:uuid:" + triple.getSubject().substring(2));
            }
            if (isBlankNode(triple.getObject())) {
                triple.setObject("urn:uuid:" + triple.getObject().substring(2));
            }
        }
        return triples;
    }

    private JSONArray getGraphNamesVirtuosoSparql(String baseGraphName) throws IOException, URISyntaxException, AuthenticationException, MalformedChallengeException {
        // Your code to execute the SPARQL query to retrieve graph names
        // Use the following SPARQL query:
        String sparqlQuery = "SELECT DISTINCT ?graph WHERE { GRAPH ?graph { ?s ?p ?o } "
                + "FILTER(CONTAINS(STR(?graph), \"" + baseGraphName + "\")) }";

        // Return the graph names as a JSONArray
        return querySparql(sparqlQuery).getJSONArray("bindings");
    }
}

//    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//        DELETE {
//        GRAPH <https://spec.industrialontologies.org/ontology/202401/core/Core/> {
//        ?subject ?predicate ?object
//        }
//        }
//        WHERE {
//        GRAPH <https://spec.industrialontologies.org/ontology/202401/core/Core/> {
//        VALUES (?subject ?predicate ?object) {
//        (406c8018-86e9-4ca2-b498-9290f3d18a4e <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <https://spec.industrialontologies.org/ontology/core/Core/ObjectiveSpecification>)
//        (406c8018-86e9-4ca2-b498-9290f3d18a4e <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> 3367f5cb-b12d-456d-94a9-1d52e5354d14)
//        (6369a3c4-a6b2-474d-a406-421f0792b1b9 <http://www.w3.org/2002/07/owl#intersectionOf> 406c8018-86e9-4ca2-b498-9290f3d18a4e)
//        (<https://spec.industrialontologies.org/ontology/core/Core/BusinessProcess> <http://www.w3.org/2000/01/rdf-schema#subClassOf> 6369a3c4-a6b2-474d-a406-421f0792b1b9)
//        (3367f5cb-b12d-456d-94a9-1d52e5354d14 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <https://spec.industrialontologies.org/ontology/core/Core/PlanSpecification>)
//        (3367f5cb-b12d-456d-94a9-1d52e5354d14 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)
//        }
//        }
//        }
//
//
//        DELETE DATA {
//            GRAPH <https://spec.industrialontologies.org/ontology/202401/core/Core/> {
//                 <3367f5cb-b12d-456d-94a9-1d52e5354d>
//                 <http://www.w3.org/2000/01/rdf-schema%23subClassOf>
//                 <https://spec.industrialontologies.org/ontology/core/Core/PlanSpecification> .
//            }
//        }


