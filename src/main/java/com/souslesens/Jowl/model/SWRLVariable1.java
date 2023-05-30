package com.souslesens.Jowl.model;

import java.util.List;

public class SWRLVariable1 {
		String type;
		List<SWRLVariables> entities;
		public SWRLVariable1 (String type , List<SWRLVariables> entities) {
			this.entities = entities;
			this.type = type;
		}
	    public String getType() {
	        return type;
	    }

	    public List<SWRLVariables> getEntities() {
	        return entities;
	    }
		
}
