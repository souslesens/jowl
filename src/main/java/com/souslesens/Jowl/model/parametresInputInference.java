package com.souslesens.Jowl.model;

public class parametresInputInference {

		private Boolean equivalentClass;
	    private Boolean sameIndividual;
	    private Boolean differentIndividual;
	    private Boolean IntersectionOf;
	    private Boolean UnionOf;
	    private Boolean DisjointClasses;
	    private Boolean HasValue;
	    private Boolean InverseObjectProperties;
	    private Boolean AllValuesFrom;
	    private Boolean SomeValuesFrom;
	    private Boolean DomainAndRange;
	    
	    
	    public parametresInputInference() {
	    }

	    public parametresInputInference(Boolean equivalentClass,Boolean sameIndividual, Boolean differentIndividual, Boolean IntersectionOf,Boolean UnionOf, Boolean DisjointClasses, Boolean HasValue, Boolean InverseObjectProperties, Boolean AllValuesFrom , Boolean SomeValuesFrom , Boolean DomainAndRange ) {
	        this.equivalentClass = equivalentClass;
	        this.sameIndividual = sameIndividual;
	        this.differentIndividual = differentIndividual;
	        this.IntersectionOf = IntersectionOf;
	        this.UnionOf = UnionOf;
	        this.DisjointClasses = DisjointClasses;
	        this.HasValue = HasValue; 
	        this.InverseObjectProperties = InverseObjectProperties;
	        this.AllValuesFrom = AllValuesFrom;
	        this.SomeValuesFrom = SomeValuesFrom;
	        this.DomainAndRange = DomainAndRange;
	    }

	    public Boolean getEquivalentClass() {
	        return equivalentClass;
	    }

	    public void setEquivalentClass(Boolean equivalentClass) {
	        this.equivalentClass = equivalentClass;
	    }

	    public Boolean getSameIndividual() {
	        return sameIndividual;
	    }

	    public void setSameIndividual(Boolean sameIndividual) {
	        this.sameIndividual = sameIndividual;
	    }

	    public Boolean getDifferentIndividual() {
	        return differentIndividual;
	    }

	    public void setDifferentIndividual(Boolean differentIndividual) {
	        this.differentIndividual = differentIndividual;
	    }
	    
	    public Boolean getIntersectionOf() {
	        return IntersectionOf;
	    }

	    public void setIntersectionOf(Boolean IntersectionOf) {
	        this.IntersectionOf = IntersectionOf;
	    }

	    public Boolean getUnionOf() {
	        return UnionOf;
	    }

	    public void setUnionOf(Boolean UnionOf) {
	        this.UnionOf = UnionOf;
	    }
	    
	    public Boolean getDisjointClasses() {
	        return DisjointClasses;
	    }

	    public void setDisjointClasses(Boolean DisjointClasses) {
	        this.DisjointClasses = DisjointClasses;
	    }
	    
	    public Boolean getHasValue() {
	        return HasValue;
	    }

	    public void setHasValue(Boolean HasValue) {
	        this.HasValue = HasValue;
	    }
	    
	    public Boolean getInverseObjectProperties() {
	        return InverseObjectProperties;
	    }

	    public void setInverseObjectProperties(Boolean InverseObjectProperties) {
	        this.InverseObjectProperties = InverseObjectProperties;
	    }
	    
	    public Boolean getAllValuesFrom() {
	        return AllValuesFrom;
	    }

	    public void setAllValuesFrom(Boolean AllValuesFrom) {
	        this.AllValuesFrom = AllValuesFrom;
	    }
	    
	    public Boolean getSomeValuesFrom() {
	        return SomeValuesFrom;
	    }

	    public void setSomeValuesFrom(Boolean SomeValuesFrom) {
	        this.SomeValuesFrom = SomeValuesFrom;
	    }

	    public Boolean getDomainAndRange() {
	        return DomainAndRange;
	    }

	    public void setDomainAndRange(Boolean DomainAndRange) {
	        this.DomainAndRange = DomainAndRange;
	    }

	    @Override
	    public String toString() {
	        return "{" +
	                "\"equivalentClass\":\"" + equivalentClass + "\"," +
	                "\"sameIndividual\":\"" + sameIndividual + "\"," +
	                "\"IntersectionOf\":\"" + IntersectionOf + "\"," +
	                "\"UnionOf\":\"" + UnionOf + "\"," +
	                "\"DisjointClasses\":\"" + DisjointClasses + "\"," +
	                "\"differentIndividual\":\"" + differentIndividual + "\"," +
	                "\"HasValue\":\"" + HasValue + "\"," +
	                "\"InverseObjectProperties\":\"" + InverseObjectProperties + "\"," +
	                "\"AllValuesFrom\":\"" + AllValuesFrom + "\"," +
	                "\"SomeValuesFrom\":\"" + SomeValuesFrom + "\"," +
	                "\"DomainAndRange\":\"" + DomainAndRange + "\"" +
	                "}";
	    }
	}

	
	

