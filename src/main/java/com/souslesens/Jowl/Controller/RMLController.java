package com.souslesens.Jowl.Controller;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.souslesens.Jowl.model.MappingRequest;
import com.souslesens.Jowl.services.RMLProcessorService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@RestController
@RequestMapping("/rml")
public class RMLController {
	
	@Autowired
    private  RMLProcessorService rmlProcessorService;


    @PostMapping("/mapping")
    public ResponseEntity<String> performMapping(@RequestBody MappingRequest mappingRequest) {
        try {
            Model model = rmlProcessorService.performRmlMappingMultiSource(mappingRequest.getRml(), mappingRequest.getSources());
            StringWriter stringWriter = new StringWriter();
            Rio.write(model, stringWriter, RDFFormat.TURTLE);
            return ResponseEntity.ok(stringWriter.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during the RML mapping process: " + e.getMessage());
        }
    }
    @PostMapping("/validateRML")
    public ResponseEntity<String> validateRmlString(@RequestBody Map<String, String> body) {
        String rml = body.get("rml");
        if (rml.isEmpty()) {
            return new ResponseEntity<>("No RML provided", HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = new ByteArrayInputStream(rml.getBytes(StandardCharsets.UTF_8))) {
            Rio.parse(inputStream, "", RDFFormat.TURTLE);
            return new ResponseEntity<>("RML is valid", HttpStatus.OK);

        } catch (RDFParseException e) {
            return new ResponseEntity<>("RML is invalid. Error at line " + e.getLineNumber() + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("RML  is invalid: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
