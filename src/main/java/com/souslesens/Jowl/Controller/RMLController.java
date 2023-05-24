package com.souslesens.Jowl.Controller;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.souslesens.Jowl.services.RMLProcessorService;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


@RestController
@RequestMapping("/rml")
public class RMLController {
	
	@Autowired
    private  RMLProcessorService rmlProcessorService;


    @PostMapping("/mapping")
    public ResponseEntity<String> performMapping(
            @RequestParam("rml") MultipartFile rmlFile,
            @RequestParam("data") MultipartFile dataFile,
            @RequestParam("format") String format) {


        try {
            Model model = rmlProcessorService.performRmlMapping(rmlFile, dataFile, format);
            StringWriter stringWriter = new StringWriter();
            Rio.write(model, stringWriter, RDFFormat.TURTLE);
            return ResponseEntity.ok(stringWriter.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during the RML mapping process: " + e.getMessage());
        }
    }
    @PostMapping("/validateRML")
    public ResponseEntity<String> validateRmlFile(@RequestParam("file") MultipartFile rmlFile) {
        if (rmlFile.isEmpty()) {
            return new ResponseEntity<>("No file provided", HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = rmlFile.getInputStream()) {
            Rio.parse(inputStream, "", RDFFormat.TURTLE);
            return new ResponseEntity<>("RML file is valid", HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("Failed to read the file", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RDFParseException e) {
            return new ResponseEntity<>("RML file is invalid. Error at line " + e.getLineNumber() + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("RML file is invalid: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
