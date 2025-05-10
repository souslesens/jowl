package com.souslesens.Jowl.model;

public class GetAllAxiomsInput {

    private final String graphName;
    private final boolean reload;


    public String getGraphName() {
        return graphName;
    }



    public boolean isReload() {
        return reload;
    }

    public GetAllAxiomsInput(String graphName,  Boolean reload) {
        this.graphName = graphName;
        this.reload =  (reload != null) ? reload : true;

    }



}


