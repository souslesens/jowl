package com.souslesens.Jowl.model;

public class SWRLVariables {
	String name;
	String[] var;
	String[] literal;
	
	public SWRLVariables(String name,String[] var,String[] literal) {
		this.name = name;
		this.var = var;
		this.literal=literal;
	}
	
	public String getName() {
		return name;
	}
	public String[] getVar() {
		return var;
	}
	
	public String[] getLiteral() {
		return literal;
	}
}
