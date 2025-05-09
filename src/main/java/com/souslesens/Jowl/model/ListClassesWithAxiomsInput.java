package com.souslesens.Jowl.model;

public class ListClassesWithAxiomsInput {

    private final String graphName;
    private final String axiomType;

    private final boolean complex;

    public String getGraphName() {
        return graphName;
    }

    public String getAxiomType() {
        return axiomType;
    }

    public boolean isComplex() {
        return complex;
    }

    public ListClassesWithAxiomsInput(String graphName, String axiomType, Boolean complex) {
        this.graphName = graphName;
        this.axiomType = (axiomType != null) ? axiomType.toLowerCase() : "";
        this.complex = (complex != null) ? complex : true;
    }


}


