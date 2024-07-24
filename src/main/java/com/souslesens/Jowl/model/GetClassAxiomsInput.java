package com.souslesens.Jowl.model;

public class GetClassAxiomsInput {

    private final String graphName;
    private final String classUri;
    private final String axiomType;
    private final boolean triplesFormat;
    private final boolean manchesterFormat;

    public GetClassAxiomsInput(String graphName, String classUri, String axiomType, Boolean triplesFormat, Boolean manchesterFormat) {
        this.graphName = graphName;
        this.classUri = classUri;
        this.axiomType = (axiomType != null) ? axiomType.toLowerCase() : "";
        this.triplesFormat = (triplesFormat != null) ? triplesFormat : true;
        this.manchesterFormat = (manchesterFormat != null) ? manchesterFormat : true;
    }

    public String getGraphName() {
        return graphName;
    }

    public String getClassUri() {
        return classUri;
    }

    public String getAxiomType() {
        return axiomType;
    }

    public boolean isTriplesFormat() {
        return triplesFormat;
    }

    public boolean isManchesterFormat() {
        return manchesterFormat;
    }
}


