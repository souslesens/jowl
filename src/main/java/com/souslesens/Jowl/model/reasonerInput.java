package com.souslesens.Jowl.model;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class reasonerInput {
	private String filePath;
	private String url;
	private String ontologyContent;
	
	public reasonerInput(String filePath , String url, String ontologyContent) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContent = ontologyContent;
	}
	public String getFilePath() {
		return filePath;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getOntologyContent() {
		return ontologyContent;
	}

}
