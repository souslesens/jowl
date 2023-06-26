package com.souslesens.Jowl.services;


import java.util.List;

import org.eclipse.rdf4j.model.Model;

import com.souslesens.Jowl.model.Source;

public interface RMLProcessorService {

	public Model performRmlMappingMultiSource(String rmlString, List<Source> sources) throws Exception;
	
}
