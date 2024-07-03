package com.souslesens.Jowl.model;

public class ManchesterToTriplesInput {

    private final String graphName;
    private final String input;

    private final String classUri;

    private final String axiomType;
    private final boolean saveTriples;
    private final boolean checkConsistency;


    public ManchesterToTriplesInput(String graphName, String input, String classUri, String axiomType, Boolean checkConsistency, Boolean saveTriples) {
        this.graphName = graphName;
        this.input = input;
        this.classUri = classUri;
        this.axiomType = axiomType;
        this.saveTriples = (saveTriples != null) ? saveTriples : false;
        this.checkConsistency = this.saveTriples || ((checkConsistency != null) ? checkConsistency : false);
    }

    public String getGraphName() {
        return graphName;
    }

    public String getInput() {
        return input;
    }

    public String getClassUri() {
        return classUri;
    }

    public String getAxiomType() {
        return axiomType;
    }

    public boolean isSaveTriples() {
        return saveTriples;
    }

    public boolean isCheckConsistency() {
        return checkConsistency;
    }
}


