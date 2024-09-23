package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.model.*;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.model.exceptions.ParsingAxiomException;
import com.souslesens.Jowl.services.AxiomsService;
import com.souslesens.Jowl.services.VirtuosoService;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.json.JSONException;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

@RestController
@RequestMapping(value="/axioms")
public class AxiomsController {

    @Autowired
    AxiomsService axiomsService;

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
            OWLAxiom axiom = axiomsService.parseStringToAxiom(graphName, input);
            System.out.print("graphName: " + graphName + " input: " + input + " axiom: " + axiom);
            if (axiom == null) {
                return ResponseEntity.badRequest().body("Error parsing axiom");
            }

            ArrayList<jenaTripleParser> triples = axiomsService.getTriples(axiom);

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
            OWLAxiom axiom = axiomsService.parseStringToAxiom(graphName, input);
            System.out.print("graphName: " + graphName + " input: " + input + " axiom: " + axiom);
            if (axiom == null) {
                return ResponseEntity.badRequest().body("Error parsing axiom");
            }

            return axiomsService.checkManchesterAxiomConsistency(graphName, axiom) ? ResponseEntity.ok().body("true") : ResponseEntity.status(422).body("false");

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
        String axiomGraphName = request.getAxiomGraphName();
        if ((graphName == null || graphName.isEmpty() || triples == null || triples.length == 0)
                && (axiomGraphName == null || axiomGraphName.isEmpty())) {
            return ResponseEntity.badRequest().body("Either (graphName and triples) or axiomGraphName must be provided.");
        }

        if (graphName != null && !graphName.isEmpty()) {
            try {
                return ResponseEntity.ok(axiomsService.triplesToManchester(graphName, triples));
            } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Error while reading ontology From Triple Store");
            } catch (NoVirtuosoTriplesException e) {
                e.printStackTrace();
                return ResponseEntity.status(404).body(e.getMessage());
            } catch (IOException | URISyntaxException | MalformedChallengeException | AuthenticationException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Error occurred while querying virtuoso");
            }
        }  else {
            try {
                System.out.println("getting the whole sous graph");
                return ResponseEntity.ok(axiomsService.triplesToManchester(axiomGraphName));
            } catch (IOException | URISyntaxException | MalformedChallengeException | AuthenticationException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Error occurred while querying virtuoso");
            }
        }

    }

    @PostMapping(value="/getClassAxioms")
    public ResponseEntity<String> getClassAxioms(@RequestBody GetClassAxiomsInput request) {
        String graphName = request.getGraphName();
        String classUri = request.getClassUri();
        String axiomType = request.getAxiomType();
        boolean triplesFormat = request.isTriplesFormat();
        boolean manchesterFormat = request.isManchesterFormat();

        if (graphName == null) {
            return ResponseEntity.status(400).body("graph name is required");
        } else if (classUri == null) {
            return ResponseEntity.status(400).body("class uri is required");
        } else if (!triplesFormat && !manchesterFormat) {
            return ResponseEntity.status(400).body("at least one format should be specified manchester format or triples");
       }  else if (!(axiomType.isEmpty() || axiomType.equals("subclassof") || axiomType.equals("equivalentclass")
               || axiomType.equals("disjointwith") )) {
            return ResponseEntity.status(400).body("axiom type should be speicfied as one the follwoing: subclassof, equivalentclass, disjointwith  or be leaved empty for all" );
       }

        try {
            return ResponseEntity.ok(axiomsService.getClassAxioms(graphName, classUri, axiomType, manchesterFormat, triplesFormat));
        }  catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while creating the ontology");
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
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


    @PostMapping(value="/checkTriplesConsistency")
    public ResponseEntity<String> checkTriplesConsistency(@RequestBody CheckTriplesCosistencyInput request) {

        String graphName = request.getGraphName();
        ArrayList<jenaTripleParser> triples = request.getTriples();
        if ((graphName == null || graphName.isEmpty() || triples == null || triples.size() == 0)) {
            return ResponseEntity.badRequest().body("graphName and triples must be provided.");
        }

        try {
            return axiomsService.checkTriplesConsistency(graphName, triples, false) ? ResponseEntity.ok().body("true") : ResponseEntity.status(422).body("false");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error while reading ontology From Triple Store");
        } catch (NoVirtuosoTriplesException | AuthenticationException | MalformedChallengeException | IOException |
                 URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }


    @PostMapping(value="/saveTriples")
    public ResponseEntity<String> saveTriples(@RequestBody CheckTriplesCosistencyInput request) {

        String graphName = request.getGraphName();
        ArrayList<jenaTripleParser> triples = request.getTriples();
        if ((graphName == null || graphName.isEmpty() || triples == null || triples.size() == 0)) {
            return ResponseEntity.badRequest().body("graphName and triples must be provided.");
        }

        try {
            return axiomsService.checkTriplesConsistency(graphName, triples, true) ? ResponseEntity.ok().body("triples saved successfully") : ResponseEntity.status(422).body("the triples are not consistent with the ontology, cannot save");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error while reading ontology From Triple Store");
        } catch (NoVirtuosoTriplesException | AuthenticationException | MalformedChallengeException | IOException |
                 URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }

    @PostMapping(value="/listClassesWithAxioms")
    public ResponseEntity<String> listClassesWithAxioms(@RequestBody ListClassesWithAxiomsInput request ) {
        String graphName = request.getGraphName();
        String axiomType = request.getAxiomType();
        boolean complexAxioms = request.isComplex();

        if (graphName == null || graphName.isEmpty()) {
            return ResponseEntity.badRequest().body("Graph Name should be provided");
        } else if (!(axiomType.isEmpty() || axiomType.equals("subclassof") || axiomType.equals("equivalentclasses")
                || axiomType.equals("disjointwith") )) {
            return ResponseEntity.status(400).body("axiom type should be speicfied as one the follwoing: subclassof, equivalentclasses, disjointwith  or be leaved empty for all" );
        }

        try {
            return ResponseEntity.ok(axiomsService.listClassesWithAxioms(graphName, axiomType, complexAxioms).toString());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while creating the ontology");
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        }


    }



}
