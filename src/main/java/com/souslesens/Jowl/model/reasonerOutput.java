package com.souslesens.Jowl.model;

public class reasonerOutput {
    private reasonerConsistency myBoolean;
    private reasonerUnsatisfaisability Unsatisfaisable;
    private reasonerExtractTriples triplesInference;

    public reasonerOutput() {
    	
    }
    public reasonerOutput(reasonerUnsatisfaisability Unsatisfaisable) {
    	this.Unsatisfaisable=Unsatisfaisable;
    }
    public reasonerOutput(reasonerConsistency myBoolean) {
        this.myBoolean = myBoolean;
    }
    
    public reasonerOutput(reasonerExtractTriples triplesInference) {
        this.triplesInference = triplesInference;
    }
    
    public reasonerOutput(reasonerConsistency myBoolean, reasonerUnsatisfaisability Unsatisfaisable, reasonerExtractTriples triplesInference) {
        this.myBoolean = myBoolean;
        this.Unsatisfaisable = Unsatisfaisable;
        this.triplesInference = triplesInference;
    }

    public reasonerConsistency isMyBoolean() {
        return myBoolean;
    }

    public void setMyBoolean(reasonerConsistency myBoolean) {
        this.myBoolean = myBoolean;
    }

	public reasonerExtractTriples getTriples() {
		return triplesInference;
	}

	public void setTriples(reasonerExtractTriples triplesInference) {
		this.triplesInference = triplesInference;
	}

	public reasonerUnsatisfaisability getUnsatisfaisable() {
		return Unsatisfaisable;
	}

	public void setUnsatisfaisable(reasonerUnsatisfaisability unsatisfaisable) {
		Unsatisfaisable = unsatisfaisable;
	}
}
