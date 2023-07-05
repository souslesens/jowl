package com.souslesens.Jowl.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class Source {
    private String contentEncoded64; 
    private String format;
    private String fileName;
    

    public String getContentEncoded64() {
        return this.contentEncoded64;
    }

    public void setContentEncoded64(String ContentEncoded64) {
        this.contentEncoded64 = ContentEncoded64;
    }

    public InputStream getContentEncoded64AsInputStream() {
        byte[] bytes = Base64.getDecoder().decode(this.contentEncoded64);
        return new ByteArrayInputStream(bytes);
    }

    public String getFormat() {
        return this.format;
    }

    public String getFileName() {
        return this.fileName;
    }


    public void setFormat(String format) {
        this.format = format;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
