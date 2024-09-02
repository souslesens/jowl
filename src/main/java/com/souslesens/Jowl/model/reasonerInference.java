package com.souslesens.Jowl.model;

public class reasonerInference {
	private final String filePath;
	private final String url;
	private final String ontologyContentEncoded64;
	private final String[] predicates;

	private final String graphName;
	
	public reasonerInference(String filePath , String url, String ontologyContentEncoded64 ,String[] predicates, String graphName) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContentEncoded64 = ontologyContentEncoded64;
        this.predicates = predicates;
		this.graphName = graphName;
        }
	public String getFilePath() {
		return filePath;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getOntologyContentEncoded64() {
		return ontologyContentEncoded64;
	}

	public String[] getParams() {
		return predicates;
	}

	public String getGraphName() {
		return graphName;
	}
}
