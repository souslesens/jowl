package com.souslesens.Jowl.model;

import java.util.List;

public class MappingRequest {
    private String rml;
    private List<Source> sources;

    // Getter and setter for rml
    public String getRml() {
        return rml;
    }

    public void setRml(String rml) {
        this.rml = rml;
    }

    // Getter and setter for sources
    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
}
