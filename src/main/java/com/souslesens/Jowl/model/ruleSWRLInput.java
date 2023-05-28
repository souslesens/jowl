package com.souslesens.Jowl.model;

public class ruleSWRLInput {
	private String filePath;
	private String url;
	private String ontologyContentEncoded64;
	private String[] body;
	private String head;
	
	public ruleSWRLInput(String filePath , String url, String ontologyContentEncoded64 ,String[] body,String head) {
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

	public String getHead() {
		return head;
	}
	
	public String[] getBody() {
		return body;
	}
}
