package com.souslesens.Jowl.services;

import org.eclipse.rdf4j.model.Model;
import org.springframework.web.multipart.MultipartFile;

public interface RMLProcessorService {

	Model performRmlMapping (MultipartFile rmlFile, MultipartFile dataFile,  String format) throws Exception;
	
}
