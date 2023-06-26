package com.souslesens.Jowl.services;


import be.ugent.rml.cli.Main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;

import com.souslesens.Jowl.model.Source;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Service
public class RMLProcessorServiceImpl implements RMLProcessorService {
    public Model performRmlMappingMultiSource(String rmlString, List<Source> sources) throws Exception {
        Path tempDir = Files.createTempDirectory("data-");
        Path rmlTempFile = createTempFileFromString("rml-", ".rml", rmlString, tempDir);
        Path outputTempFile = Files.createTempFile("output-", ".ttl");

        System.out.println("RML file path: " + rmlTempFile); // Debug log

        for (Source source : sources) {
            if (source.getFileContent() != null) {
                createFileFromString(source.getFileContent(), source.getFormat(), source.getFileName(), tempDir);
            } else {
                createCSVFile(source.getCsvHeaders(), source.getCsvData(), source.getFileName(), tempDir);
            }
        }

        processRMLMapping(rmlTempFile, tempDir, outputTempFile);

        Model model = generateModelFromOutput(outputTempFile);
        
        try {
            Files.deleteIfExists(outputTempFile);
            deleteDirectoryWithContents(tempDir);
            } catch (IOException e) {
            System.err.println("Failed to clean up temporary files: " + e.getMessage());
        }


        return model;
    }

    private Path createCSVFile(List<String> headers, List<List<String>> data, String filename, Path directory) throws IOException {
        Path dataTempFile = directory.resolve(filename + ".csv");
        BufferedWriter writer = Files.newBufferedWriter(dataTempFile, StandardCharsets.UTF_8);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])));
        for (List<String> row : data) {
            csvPrinter.printRecord(row);
        }
        csvPrinter.close();

        return dataTempFile;
    }

    private Path createFileFromString(String content, String format, String filename, Path directory) throws IOException {
        Path dataTempFile = directory.resolve(filename + "." + format);
        System.out.println("Saving file: " + dataTempFile); // Debug log
        Files.writeString(dataTempFile, content, StandardCharsets.UTF_8);

        return dataTempFile;
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
    
    private static void deleteDirectoryWithContents(Path pathToBeDeleted) throws IOException {
        Files.walk(pathToBeDeleted)
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);
    }
}