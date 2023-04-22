package com.souslesens.Jowl.model;

public class reasonerInput {
	private String filePath;
	private String url;
	
	public reasonerInput(String filePath , String url) {
		this.filePath = filePath;
		this.url = url;
	}
	public String getFilePath() {
		return filePath;
	}
	
	public String getUrl() {
		return url;
	}
}
