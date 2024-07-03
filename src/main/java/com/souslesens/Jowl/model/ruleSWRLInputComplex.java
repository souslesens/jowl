package com.souslesens.Jowl.model;

import java.util.List;

public class ruleSWRLInputComplex {
	private final String filePath;
	private final String url;
	private final String ontologyContentEncoded64;
	private final List<SWRLTypeEntityVariable> premise;
	private final List<SWRLTypeEntityVariable> conclusion;
	
	public ruleSWRLInputComplex(String filePath , String url, String ontologyContentEncoded64 ,List<SWRLTypeEntityVariable> premise,List<SWRLTypeEntityVariable> conclusion) {
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

	public List<SWRLTypeEntityVariable> getHead() {
		return conclusion;
	}
	
	public List<SWRLTypeEntityVariable> getBody() {
		return premise;
	}
}
