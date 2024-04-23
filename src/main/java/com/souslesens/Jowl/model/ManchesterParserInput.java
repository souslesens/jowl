package com.souslesens.Jowl.model;

import org.springframework.web.multipart.MultipartFile;

public class ManchesterParserInput {

    private String graphName;
    private String input;

    public ManchesterParserInput(String graphName, String input) {
        this.graphName = graphName;
        this.input = input;
    }

    public String getGraphName() {
        return graphName;
    }

    public String getInput() {
        return input;
    }

}


