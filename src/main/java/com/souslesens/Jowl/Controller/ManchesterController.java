package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.model.ManchesterParserInput;
import com.souslesens.Jowl.services.ManchesterService;
import org.apache.jena.base.Sys;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value="/manchester")
public class ManchesterController {

    @Autowired
    ManchesterService serviceManchester;


    @PostMapping(value="/manchester2triples")
    public ResponseEntity<String> manchester2triple(@RequestBody ManchesterParserInput request) {

        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        String input = request.getInput();
        int parametersCount = countParams(ontologyContentEncoded64, filePath, url);
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body("Manchester Syntax Input should be provided");
        } else if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("Besides the input, at least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }

        try {
            OWLAxiom axiom = serviceManchester.parseStringToAxiom(filePath, url, ontologyContentEncoded64, input);
            if (axiom == null) {
                return ResponseEntity.badRequest().body("Error parsing axiom");
            }
            return ResponseEntity.ok(axiom.toString());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
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
