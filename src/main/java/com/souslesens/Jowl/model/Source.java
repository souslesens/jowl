package com.souslesens.Jowl.model;

import java.util.List;

public class Source {
    private String fileContent;
    private String format;
    private String fileName;
    private List<String> csvHeaders;
    private List<List<String>> csvData;

    public String getFileContent() {
        return this.fileContent;
    }
    public String getFormat() {
        return this.format;
    }

    public String getFileName() {
        return this.fileName;
    }

    public List<String> getCsvHeaders() {
        return this.csvHeaders;
    }

    public List<List<String>> getCsvData() {
        return this.csvData;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCsvHeaders(List<String> csvHeaders) {
        this.csvHeaders = csvHeaders;
    }

    public void setCsvData(List<List<String>> csvData) {
        this.csvData = csvData;
    }
}
