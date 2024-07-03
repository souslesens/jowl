package com.souslesens.Jowl.model;

public class TriplesToManchesterInput {

    private final String graphName;
    private final jenaTripleParser[] triples;

    private final String axiomGraphName;

    public TriplesToManchesterInput(String graphName, jenaTripleParser[] triples, String axiomGraphName) {
        this.graphName = graphName;
        this.triples = triples;
        this.axiomGraphName = axiomGraphName;
    }

    public String getGraphName() {
        return graphName;
    }

    public jenaTripleParser[] getTriples() {
        return triples;
    }

    public String getAxiomGraphName() {
        return axiomGraphName;
    }

}


