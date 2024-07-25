package com.souslesens.Jowl.model;

public class ListClassesWithAxiomsInput {

    private final String graphName;
    private final String axiomType;

    public String getGraphName() {
        return graphName;
    }

    public String getAxiomType() {
        return axiomType;
    }

    public ListClassesWithAxiomsInput(String graphName, String axiomType) {
        this.graphName = graphName;
        this.axiomType = axiomType;
    }
}


