package com.souslesens.Jowl.model;

import java.util.List;

public class SWRLTypeEntityVariable {
		String type;
		List<SWRLVariables> entities;
		public SWRLTypeEntityVariable (String type , List<SWRLVariables> entities) {
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
