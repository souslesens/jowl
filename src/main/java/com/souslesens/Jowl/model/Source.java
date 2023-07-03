package com.souslesens.Jowl.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class Source {
	 private String data;
	 private String format;
	 private String fileName;
	 private List<String> csvHeaders;
	 private List<List<String>> csvData;


    public String getData() {
        return this.data;
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

    public void setData(String data) {
        this.data = data;
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

    public InputStream getDataAsInputStream() {
        byte[] bytes = Base64.getDecoder().decode(this.data);
        return new ByteArrayInputStream(bytes);
    }
}
