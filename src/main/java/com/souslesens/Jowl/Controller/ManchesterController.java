package com.souslesens.Jowl.Controller;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.souslesens.Jowl.model.ManchesterToTriplesInput;
import com.souslesens.Jowl.model.TriplesToManchesterInput;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.model.jenaTripleParser;
import com.souslesens.Jowl.services.ManchesterService;
import com.souslesens.Jowl.services.VirtuosoService;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.jena.base.Sys;
import org.json.JSONException;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping(value="/manchester")
public class ManchesterController {

    @Autowired
    ManchesterService serviceManchester;

    @Autowired
    VirtuosoService serviceVirtuoso;


    @PostMapping(value="/manchester2triples")
    public ResponseEntity<String> manchester2triple(@RequestBody ManchesterToTriplesInput request) {

        String graphName = request.getGraphName();
        String input = request.getInput();
        String classUri = request.getClassUri();
        String axiomType = request.getAxiomType();
        boolean saveTriples = request.isSaveTriples();
        boolean checkConsistency = request.isCheckConsistency();

        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body("Manchester Syntax Input should be provided");
        } else if (graphName == null || graphName.isEmpty()) {
            return ResponseEntity.badRequest().body("Graph Name should be provided");
        } else if (classUri == null || classUri.isEmpty()) {
            return ResponseEntity.badRequest().body("Class URI should be provided");
        } else if (axiomType == null || axiomType.isEmpty()) {
            return ResponseEntity.badRequest().body("Axiom Type should be provided");
        }

        try {
            OWLAxiom axiom = serviceManchester.parseStringToAxiom(graphName, input);
            System.out.print("graphName: " + graphName + " input: " + input + " axiom: " + axiom);
            if (axiom == null) {
                return ResponseEntity.badRequest().body("Error parsing axiom");
            }

            ArrayList<jenaTripleParser> triples = serviceManchester.getTriples(axiom);

            if (saveTriples) {
                serviceVirtuoso.saveTriples(graphName, classUri, axiomType, triples);
            }
            return ResponseEntity.ok().body(triples.toString());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while creating the ontology");
        } catch (ParsingAxiomException e) {
            e.printStackTrace();
            return ResponseEntity.status(422).body(e.getMessage());
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        } catch (MalformedChallengeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while parsing results from triple store");
        }
    }

    @PostMapping(value="/checkConsistency")
    public ResponseEntity<String> manchesterAxiomConsistencyCheck(@RequestBody ManchesterToTriplesInput request) {

        String graphName = request.getGraphName();
        String input = request.getInput();
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body("Manchester Syntax Input should be provided");
        } else if (graphName == null || graphName.isEmpty()) {
            return ResponseEntity.badRequest().body("Graph Name should be provided");
        }

        try {
            OWLAxiom axiom = serviceManchester.parseStringToAxiom(graphName, input);
            System.out.print("graphName: " + graphName + " input: " + input + " axiom: " + axiom);
            if (axiom == null) {
                return ResponseEntity.badRequest().body("Error parsing axiom");
            }

            return serviceManchester.checkManchesterAxiomConsistency(graphName, axiom) ? ResponseEntity.ok().body("true") : ResponseEntity.status(422).body("false");

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (ParsingAxiomException e) {
            e.printStackTrace();
            return ResponseEntity.status(422).body(e.getMessage());
        }
    }

    @PostMapping(value="/triples2manchester")
    public ResponseEntity<String> triples2manchester(@RequestBody TriplesToManchesterInput request) {
        String graphName = request.getGraphName();
        jenaTripleParser[] triples = request.getTriples();
        if (triples == null || triples.length == 0) {
            return ResponseEntity.badRequest().body("Triples List should be provided");
        } else if (graphName == null || graphName.isEmpty()) {
            return ResponseEntity.badRequest().body("Graph Name should be provided");
        }

        try {
            return ResponseEntity.ok(serviceManchester.triplesToManchester(graphName, triples));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error while reading ontology From Triple Store");
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IOException | URISyntaxException | MalformedChallengeException | AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error occurred while querying virtuoso");
        }

    }

    private int countParams(Object... parameters) {
        int count = 0;
        for (Object param : parameters) {
            if (param != null && !param.toString().isEmpty()) {
                count++;
            }
        }
        return count;
    }

}
