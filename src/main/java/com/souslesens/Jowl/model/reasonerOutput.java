package com.souslesens.Jowl.model;

public class reasonerOutput {
    private boolean myBoolean;
    private reasonerUnsatisfaisability Unsatisfaisable;
    private reasonerExtractTriples triples;

    public reasonerOutput() {
    	
    }
    public reasonerOutput(reasonerUnsatisfaisability Unsatisfaisable) {
    	this.Unsatisfaisable=Unsatisfaisable;
    }
    public reasonerOutput(boolean myBoolean) {
        this.myBoolean = myBoolean;
    }
    
    public reasonerOutput(reasonerExtractTriples triples) {
        this.triples = triples;
    }
    
    public reasonerOutput(boolean myBoolean, reasonerUnsatisfaisability Unsatisfaisable, reasonerExtractTriples triples) {
        this.myBoolean = myBoolean;
        this.Unsatisfaisable = Unsatisfaisable;
        this.triples = triples;
    }

    public boolean isMyBoolean() {
        return myBoolean;
    }

    public void setMyBoolean(boolean myBoolean) {
        this.myBoolean = myBoolean;
    }

	public reasonerExtractTriples getTriples() {
		return triples;
	}

	public void setTriples(reasonerExtractTriples triples) {
		this.triples = triples;
	}

	public reasonerUnsatisfaisability getUnsatisfaisable() {
		return Unsatisfaisable;
	}

	public void setUnsatisfaisable(reasonerUnsatisfaisability unsatisfaisable) {
		Unsatisfaisable = unsatisfaisable;
	}
}
