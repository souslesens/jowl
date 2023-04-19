package com.souslesens.Jowl.model;

public class reasonerInput {
	private String filePath;
	private String operation;
	
	public reasonerInput(String filePath , String operation) {
		this.filePath = filePath;
		this.operation = operation;
	}
	public String getUrl() {
		return filePath;
	}
	
	public String getOperation() {
		return operation;
	}
}
