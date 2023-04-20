package com.souslesens.Jowl.model;

import java.util.ArrayList;

public class reasonerExtractTriples {
    private String subject;
    private String predicate;
    private String object;

    public reasonerExtractTriples() {
    }

    public reasonerExtractTriples(String subject,String predicate, String object ) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
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
                "\"predicate\":\"" + predicate + "\"," +
                "\"object\":\"" + object + "\"" +
                "}";
    }
}