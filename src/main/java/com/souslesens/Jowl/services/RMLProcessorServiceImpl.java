package com.souslesens.Jowl.services;


import be.ugent.rml.cli.Main;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class RMLProcessorServiceImpl implements RMLProcessorService {
	@Override
	public Model performRmlMapping(MultipartFile rmlFile, MultipartFile dataFile,  String format) throws Exception {
		String dataFileExtension;
	    switch (format.toLowerCase()) {
	        case "csv":
	            dataFileExtension = ".csv";
	            break;
	        case "xml":
	            dataFileExtension = ".xml";
	            break;
	        case "tsv":
	            dataFileExtension = ".tsv";
	            break;
	        case "json":
	            dataFileExtension = ".json";
	            break;
	        default:
	            throw new IllegalArgumentException("Unsupported format: " + format);
	    }
		Path rmlTempFile = Files.createTempFile("rml-", ".rml");
	    Path dataTempFile = Files.createTempFile("data-", dataFileExtension);
	    Path outputTempFile = Files.createTempFile("output-", ".ttl");

	    Files.copy(rmlFile.getInputStream(), rmlTempFile, StandardCopyOption.REPLACE_EXISTING);
	    Files.copy(dataFile.getInputStream(), dataTempFile, StandardCopyOption.REPLACE_EXISTING);

	    String[] args = {
	            "-m", rmlTempFile.toString(),
	            "-o", outputTempFile.toString(),
	            "-d", dataTempFile.toString()
	    };

	    PrintStream originalOut = System.out;
	    PrintStream redirectedOut = new PrintStream(outputTempFile.toFile()); // Redirect the output to the output file
	    System.setOut(redirectedOut);

	    try {
	        Main.main(args);
	    } finally {
	        System.setOut(originalOut);
	        redirectedOut.close(); // Close the PrintStream used for redirection
	        Files.deleteIfExists(rmlTempFile);
	        Files.deleteIfExists(dataTempFile);
	    }

	    String rdfOutput = Files.readString(outputTempFile, StandardCharsets.UTF_8);
	    InputStream rdfInputStream = new ByteArrayInputStream(rdfOutput.getBytes(StandardCharsets.UTF_8));
	    Model model = Rio.parse(rdfInputStream, "", RDFFormat.TURTLE);

	    Files.deleteIfExists(outputTempFile);

	    return model;
	}
}