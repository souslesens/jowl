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

        System.out.println("RML file path: " + rmlTempFile); 

        for (Source source : sources) {
            if (source.getContentEncoded64() != null) {
                createFileFromInputStream(source.getContentEncoded64AsInputStream(), source.getFormat(), source.getFileName(), tempDir);
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
        //...

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
            "-d", dataDir.toString() 
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
