package com.souslesens.Jowl.model;

import java.util.List;

public class ruleSWRLInputComplex {
	private String filePath;
	private String url;
	private String ontologyContentEncoded64;
	private List<SWRLVariable1> body;
	private List<SWRLVariable1> head;
	
	public ruleSWRLInputComplex(String filePath , String url, String ontologyContentEncoded64 ,List<SWRLVariable1> body,List<SWRLVariable1> head) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContentEncoded64 = ontologyContentEncoded64;
        this.body = body;
        this.head = head;
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

	public List<SWRLVariable1> getHead() {
		return head;
	}
	
	public List<SWRLVariable1> getBody() {
		return body;
	}
}
