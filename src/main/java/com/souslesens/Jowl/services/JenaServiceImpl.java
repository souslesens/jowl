package com.souslesens.Jowl.services;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.stereotype.Service;

import com.souslesens.Jowl.model.jenaTripleParser;
@Service

public class JenaServiceImpl implements JenaService {

	

	@Override
	public List<jenaTripleParser> getTriples(String filePath, String url,
			String ontologyContentEncoded64) {
		System.out.println(filePath+""+url+""+ontologyContentEncoded64);
		ArrayList<jenaTripleParser> tripleParser = new ArrayList<>();
		Model m = ModelFactory.createDefaultModel();
		

        try {
            if (filePath != null && url == null && ontologyContentEncoded64 == null) {
            	m = RDFDataMgr.loadModel(filePath, RDFLanguages.filenameToLang(filePath));
            } else if (url != null && filePath == null && ontologyContentEncoded64 == null) {
            	m = RDFDataMgr.loadModel(url, RDFLanguages.filenameToLang(url));
            } else if (ontologyContentEncoded64 != null && filePath == null && url == null) {
                byte[] ontologyBytes =  Base64.getMimeDecoder().decode(ontologyContentEncoded64);
                try (InputStream inputStream = new ByteArrayInputStream(ontologyBytes)) {
                	System.out.println(inputStream); // debugging 
                	 m.read(inputStream, null);
                }
            } else {
                return null;
            }
            	 
 	            StmtIterator sIter = m.listStatements();

 	            while (sIter.hasNext()) {
 	                Statement statement = sIter.next();

 	                String objectStr;
 	                RDFNode objectNode = statement.getObject();

 	                if (objectNode.isResource()) {
 	                    objectStr = "<" + objectNode.toString() + ">";
 	                } else {
 	                    objectStr = "'" + objectNode.toString().replaceAll("'", "") + "'";
 	                    objectStr = objectStr.replaceAll("\n", "");
 	                    objectStr = objectStr.replaceAll("\r", "");
 	                    objectStr = objectStr.replaceAll("\"", "");
 	                    objectStr = objectStr.replaceAll("\\\\", "");
 	                }
 	                String subject = "<" + statement.getSubject().toString() + ">";
 	                String predicate = "<" + statement.getPredicate().toString() + ">";
 	                String object =   objectStr ;

 	               tripleParser.add(new jenaTripleParser(subject, predicate, object));
 	               
 	            }
 	           return tripleParser;
            }
 	            catch (Exception e) {
 	            	return null;
				}
			
		
	}
}

