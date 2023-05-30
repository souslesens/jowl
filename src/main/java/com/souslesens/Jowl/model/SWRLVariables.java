package com.souslesens.Jowl.model;

public class SWRLVariables {
	String name;
	String[] var;
	
	public SWRLVariables(String name,String[] var) {
		this.name = name;
		this.var = var;
	}
	
	public String getName() {
		return name;
	}
	public String[] getVar() {
		return var;
	}
}
