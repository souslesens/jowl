package com.souslesens.Jowl.model;

import java.util.ArrayList;

public class CheckTriplesCosistencyInput {

    private final String graphName;
    private final ArrayList<jenaTripleParser> triples;

    public CheckTriplesCosistencyInput(String graphName, ArrayList<jenaTripleParser> triples, boolean save) {
        this.graphName = graphName;
        this.triples = triples;
    }

    public String getGraphName() {
        return graphName;
    }

    public ArrayList<jenaTripleParser> getTriples() {
        return triples;
    }

}


