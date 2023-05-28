package com.souslesens.Jowl.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.souslesens.Jowl.model.reasonerInput;
import com.souslesens.Jowl.services.SWRLService;


@RestController
@RequestMapping("/SWRL")
public class SWRLController {

	@Autowired
	private SWRLService SWRLService;
    //Post API For STRING
    @PostMapping("/insertRule")
    public ResponseEntity<?> postUnsatisfiable(@RequestBody(required = false) reasonerInput request) { 
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
    	byte[] ontologyContentDecoded64Bytes = null;
    	String ontologyContentDecoded64 = null;
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
                result = SWRLService.SWRLruleMeth1(filePath, url);
            	}else {
            	result = SWRLService.SWRLruleMeth2(ontologyContentDecoded64);
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
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
