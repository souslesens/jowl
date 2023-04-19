package com.souslesens.Jowl.model;


import org.semanticweb.owlapi.model.OWLClass;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class reasonerUnsatisfaisability {
	private OWLClass[] Unsatisfaisable;

	public reasonerUnsatisfaisability() {
        this.Unsatisfaisable = new OWLClass[100]; // initialize with a maximum capacity of 100 elements
    }
	
	public OWLClass[] getUnsatisfaisable() {
		return Unsatisfaisable;
	}

	public void setUnsatisfaisable(OWLClass[] unsatisfaisable) {
		Unsatisfaisable = unsatisfaisable;
	}

}
