package com.souslesens.Jowl.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.souslesens.Jowl.model.SWRLTypeEntityVariable;
import com.souslesens.Jowl.model.ruleSWRLInput;
import com.souslesens.Jowl.model.ruleSWRLInputComplex;
import com.souslesens.Jowl.services.SWRLService;


@RestController
@RequestMapping("/SWRL")
public class SWRLController {

	@Autowired
	private SWRLService SWRLService;
    //Post API For STRING
    @PostMapping("/insertRuleReclassification")
    public ResponseEntity<?> postReclassification(@RequestBody(required = false) ruleSWRLInput request) { 
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
    	byte[] ontologyContentDecoded64Bytes = null;
    	String ontologyContentDecoded64 = null;
        String[] reqBodies = request.getBody();
        String[] reqHead = request.getHead();
        if (reqBodies.length == 0 || reqHead.length == 0) {
        	return ResponseEntity.badRequest().body("Body or Head are empty");
        }
    	if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
    	 ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
    	 ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
    	}
    	int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result ;
            	if (!(filePath == null) || !(url == null) ) {
                result = SWRLService.SWRLruleReclassification(filePath, url,reqBodies,reqHead);
            	}else {
            	result = SWRLService.SWRLruleReclassificationB64(ontologyContentDecoded64,reqBodies,reqHead);
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    
    @PostMapping("/insertRulePropertyVA")
    public ResponseEntity<?> postPropertyValueAssignment(@RequestBody(required = false) ruleSWRLInputComplex request) { 
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
    	byte[] ontologyContentDecoded64Bytes = null;
    	String ontologyContentDecoded64 = null;
    	List<SWRLTypeEntityVariable> reqBodies = request.getBody();
    	List<SWRLTypeEntityVariable> reqHead = request.getHead();
    	if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
    	 ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
    	 ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
    	}
    	int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result = null;
            	if (!(filePath == null) || !(url == null) ) {
            		result = SWRLService.SWRLruleVABUF(filePath,url,reqBodies,reqHead);
            	}else {
            		result = SWRLService.SWRLruleVAB64(ontologyContentDecoded64,reqBodies,reqHead);
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    
    // This to count the number of the parameteres that will help us to know how many parameteres we passed to the API 
    // and we can return errors if the number of parameters is more than one
    private int countNumberOfParametres(Object... parameters) {
        int nb = 0;
        for (Object prms : parameters) {
            if (prms != null && !prms.toString().isEmpty()) {
            	nb++;
            }
        }
        return nb;
    }
	
}
