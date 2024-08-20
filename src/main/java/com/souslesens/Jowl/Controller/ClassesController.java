package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.model.ListClassesWithAxiomsInput;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import com.souslesens.Jowl.services.ClassesService;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/classes")
public class ClassesController {

    @Autowired
    ClassesService classesService;

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
            return ResponseEntity.ok(classesService.listClassesWithAxioms(graphName, axiomType, complexAxioms).toString());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while creating the ontology");
        } catch (NoVirtuosoTriplesException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(e.getMessage());
        }


    }


}
