package com.souslesens.Jowl.services;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.stereotype.Service;

import com.souslesens.Jowl.model.jenaTripleParser;
@Service

public class JenaServiceImpl implements JenaService {

	

	@Override
	public List<jenaTripleParser> getTriples(String filePath, String Url,
			String ontologyContentDecoded64) {
		ArrayList<jenaTripleParser> tripleParser = new ArrayList<>();
		Model m = ModelFactory.createDefaultModel();
		
            try {
            	 m.read(Url);
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

