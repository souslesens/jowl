package com.souslesens.Jowl.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.souslesens.Jowl.model.jenaTripleParser;
import com.souslesens.Jowl.model.jenaTripleParserInput;
import com.souslesens.Jowl.services.JenaService;

@RestController
@RequestMapping(value="/jena")
public class JenaController { 
	
	@Autowired
		JenaService serviceJena;
	
    @PostMapping("/rdftriple")
    public ResponseEntity<?> rdfToTriples(@RequestBody jenaTripleParserInput request ){
    	
        // extract input parameters from the request object
        String filePath = request.getFilePath();
        String url = request.getUrl();
        System.out.println(url);
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        int parametersCount = countParams(ontologyContentEncoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        		try {
        			List<jenaTripleParser> result = serviceJena.getTriples(filePath, url, ontologyContentEncoded64) ;
   	              return ResponseEntity.ok(result);
				} catch (Exception e) {
					return ResponseEntity.badRequest().body("Error");
				}
        }
    
    private int countParams(Object... parameters) {
        int count = 0;
        for (Object param : parameters) {
            if (param != null && !param.toString().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    	    
    	      
}





///


