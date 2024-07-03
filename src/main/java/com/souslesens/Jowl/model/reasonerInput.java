package com.souslesens.Jowl.model;

public class reasonerInput {
	private final String filePath;
	private final String url;
	private final String ontologyContentEncoded64;
	
	public reasonerInput(String filePath , String url, String ontologyContentEncoded64) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContentEncoded64 = ontologyContentEncoded64;
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

}
