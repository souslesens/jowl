package com.souslesens.Jowl.model;

public class TriplesToManchesterInput {

    private String graphName;
    private jenaTripleParser[] triples;

    public TriplesToManchesterInput(String graphName, jenaTripleParser[] triples) {
        this.graphName = graphName;
        this.triples = triples;
    }

    public String getGraphName() {
        return graphName;
    }

    public jenaTripleParser[] getTriples() {
        return triples;
    }
}


