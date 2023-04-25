package com.souslesens.Jowl.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerInput;
import com.souslesens.Jowl.services.ReasonerService;

@RestController
@RequestMapping("/reasoner")
public class ReasonerController {

    @Autowired
    private ReasonerService reasonerService;
    @GetMapping(value = "/test")
    public ResponseEntity<String> pong() 
    {
        
        return new ResponseEntity<String>("Test du serveur Spring: "+HttpStatus.OK.name(), HttpStatus.OK);
    }


    @GetMapping("/unsatisfiable")
    public ResponseEntity<?> getUnsatisfaisableClasses(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams( filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    @GetMapping("/consistency")
    public ResponseEntity<?> getConsistency(@RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        
    		int parametersCount = countParams( filePath, url);
        	if (parametersCount == 0) {
            	return ResponseEntity.badRequest().body("At least one of params should be provided");
        	} else if (parametersCount > 1) {
            	return ResponseEntity.badRequest().body("Only one of params should be provided");
        	}
            try {
                String result = reasonerService.getConsistency(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("ERROR");
            }
            
        	
        
    }
    @GetMapping("/inference")
    public ResponseEntity<?> getInference(@RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        
    		int parametersCount = countParams( filePath, url);
        	if (parametersCount == 0) {
            	return ResponseEntity.badRequest().body("At least one of params should be provided");
        	} else if (parametersCount > 1) {
            	return ResponseEntity.badRequest().body("Only one of params should be provided");
        	}
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("ERROR");
            }
            
        	
        
    }
    //Post API For STRING
    @PostMapping("/consistency")
    public ResponseEntity<?> postConsistency(@RequestParam(required = false) String filePath,@RequestParam(required = false) String url,@RequestParam(required = false) String ontologyContentEncoded64) { 
        byte[] ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
    	String ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
    	System.out.println("EYYYYYYYYYYY"+ontologyContentDecoded64);
    	int parametersCount = countParams(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getConsistencyTest(filePath, url,ontologyContentDecoded64);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    
       // ALTERNATIVE TO INPUT
    // POST API to expose consistency
    @PostMapping("/consistencyAlt")
    public ResponseEntity<?> postConsistencyAlt(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getConsistencyAlt(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    

    
    
    // POST API Alternative solution to expose inference
    @PostMapping("/inferenceAlt")
    public ResponseEntity<?> postInferenceAlt(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferencesAlt(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
   
    @PostMapping("/unsatisfiableAlt")
    public ResponseEntity<?> postUnsatisfaisableClassesAlt(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getUnsatisfaisableClassesAlt(filePath, url,ontologyFile);
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
