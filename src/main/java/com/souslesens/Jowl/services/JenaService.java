package com.souslesens.Jowl.services;

import java.util.List;

import com.souslesens.Jowl.model.jenaTripleParser;

public interface JenaService {

	
	List<jenaTripleParser> getTriples(String filePath, String Url,String ontologyContentDecoded64 );
}
