package com.souslesens.Jowl.model;

public class ruleSWRLInput {
	private final String filePath;
	private final String url;
	private final String ontologyContentEncoded64;
	private final String[] premise;
	private final String[] conclusion;
	
	public ruleSWRLInput(String filePath , String url, String ontologyContentEncoded64 ,String[] premise,String[] conclusion) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContentEncoded64 = ontologyContentEncoded64;
        this.premise = premise;
        this.conclusion = conclusion;
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

	public String[] getHead() {
		return conclusion;
	}
	
	public String[] getBody() {
		return premise;
	}
}
