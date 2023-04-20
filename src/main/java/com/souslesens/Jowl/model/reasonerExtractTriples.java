package com.souslesens.Jowl.model;

import java.util.ArrayList;

public class reasonerExtractTriples {
    private String subject;
    private String property;
    private String object;

    public reasonerExtractTriples() {
    }

    public reasonerExtractTriples(String subject, String property, String object) {
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "{" +
                "\"subject\":\"" + subject + "\"," +
                "\"property\":\"" + property + "\"," +
                "\"object\":\"" + object + "\"" +
                "}";
    }
}