package com.souslesens.Jowl.services;


import com.souslesens.Jowl.model.Source;


import be.ugent.rml.cli.Main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class RMLProcessorServiceImpl implements RMLProcessorService {
	 @Value("${application.tempFilePath}")
	    private String baseTempDirPath;

	    public Model performRmlMappingMultiSource(String rmlString, List<Source> sources) throws Exception {
	        Path tempDir = Paths.get(baseTempDirPath, "Mapping");

	        if(!Files.exists(tempDir)) {
	            Files.createDirectories(tempDir);
	        }

	        Path rmlTempFile = createTempFileFromString("rml-", ".rml", rmlString, tempDir);
	        Path outputTempFile = Files.createTempFile(tempDir, "output-", ".ttl");

	        System.out.println("RML file path: " + rmlTempFile); // Debug log

	        for (Source source : sources) {
	            if (source.getData() != null) {
	                createFileFromInputStream(source.getDataAsInputStream(), source.getFormat(), source.getFileName(), tempDir);
	            } 
	        }

	        processRMLMapping(rmlTempFile, tempDir, outputTempFile);

	        Model model = generateModelFromOutput(outputTempFile);
	        
	        try {
	            Files.deleteIfExists(outputTempFile);
	            Files.walk(tempDir).filter(Files::isRegularFile).forEach(path -> {
	                try {
	                    Files.delete(path);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            });
	        } catch (IOException e) {
	            System.err.println("Failed to clean up temporary files: " + e.getMessage());
	        }

	        return model;
	    }

	    private Path createFileFromInputStream(InputStream content, String format, String filename, Path directory) throws IOException {
	        Path dataTempFile = directory.resolve(filename + "." + format);
	        System.out.println("Saving file: " + dataTempFile); 
	        try (OutputStream os = Files.newOutputStream(dataTempFile)) {
	            byte[] buffer = new byte[1024];
	            int length;
	            while ((length = content.read(buffer)) != -1) {
	                os.write(buffer, 0, length);
	            }
	        }
	        if ("csv".equals(format)) {
	            return createCSVFileFromExistingFile(dataTempFile, directory);
	        }


	        return dataTempFile;
	    }

	    private Path createCSVFileFromExistingFile(Path existingFilePath, Path directory) throws IOException {
	        List<String> lines = Files.readAllLines(existingFilePath, StandardCharsets.UTF_8);

	        if (lines.isEmpty()) {
	            return existingFilePath;
	        }

	        List<String> newLines = new ArrayList<>();
	        String[] headers = lines.get(0).split("[;:|]"); // Split by semicolon, colon, or pipe
	        for (int i = 0; i < headers.length; i++) {
	            headers[i] = headers[i].trim().replaceAll("[^a-zA-Z0-9,]", ""); // Remove all non-alphanumeric characters
	            char c[] = headers[i].toCharArray();
	            headers[i] = new String(c);
	        }
	        newLines.add(String.join(",", headers));

	        for (int i = 1; i < lines.size(); i++) {
	            String[] data = lines.get(i).split("[;:|]"); // Split by semicolon, colon, or pipe
	            for (int j = 0; j < data.length; j++) {
	                data[j] = data[j].trim(); // Remove leading and trailing white spaces
	            }
	            newLines.add(String.join(",", data));
	        }

	        Files.write(existingFilePath, newLines, StandardCharsets.UTF_8);

	        return existingFilePath;
	    }


	    private Path createTempFileFromString(String prefix, String suffix, String content, Path directory) throws IOException {
	        Path tempFile = directory.resolve(prefix + suffix);
	        Files.writeString(tempFile, content, StandardCharsets.UTF_8);

	        return tempFile;
	    }

	    private void processRMLMapping(Path rmlTempFile, Path dataDir, Path outputTempFile) throws Exception {
	        String[] args = {
	            "-m", rmlTempFile.toString(),
	            "-o", outputTempFile.toString(),
	            "-d", dataDir.toString() // directory where the data files are
	        };

	        PrintStream originalOut = System.out;
	        PrintStream redirectedOut = new PrintStream(outputTempFile.toFile());
	        System.setOut(redirectedOut);

	        try {
	            Main.main(args);
	        } finally {
	            System.setOut(originalOut);
	            redirectedOut.close();
	        }
	    }

	    private Model generateModelFromOutput(Path outputTempFile) throws IOException {
	        String rdfOutput = Files.readString(outputTempFile, StandardCharsets.UTF_8);
	        InputStream rdfInputStream = new ByteArrayInputStream(rdfOutput.getBytes(StandardCharsets.UTF_8));
	        Model model = Rio.parse(rdfInputStream, "", RDFFormat.TURTLE);

	        return model;
	    }
	    


	}
