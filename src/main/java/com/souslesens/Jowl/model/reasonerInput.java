package com.souslesens.Jowl.model;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class reasonerInput {
	private String filePath;
	private String url;
	private MultipartFile ontologyContent;
	
	public reasonerInput(String filePath , String url, MultipartFile ontologyContent) {
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
	
	public MultipartFile getOntologyContent() {
		return ontologyContent;
	}

}
