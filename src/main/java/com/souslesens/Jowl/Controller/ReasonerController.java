package com.souslesens.Jowl.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.souslesens.Jowl.model.reasonerExtractTriples;
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
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url,null);
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
                String result = reasonerService.getConsistency(filePath, url,null);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("ERROR");
            }
            
        	
        
    }
    @GetMapping("/inference")
    public ResponseEntity<?> getInference(@RequestParam(required = false) String filePath,
                                           @RequestParam(required = false) String url) {
        if (url != null && !url.isEmpty() && filePath == null) {
        	if (url.startsWith("http") || url.startsWith("ftp")) {
            try {
            	List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url,null);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
            }
        	else {
            	return ResponseEntity.badRequest().build();
            }
        } else if (url == null && !filePath.isEmpty() && filePath != null) {
            try {
            	List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url,null);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // POST API to expose consistency
    @PostMapping("/consistency")
    public ResponseEntity<?> postConsistency(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getConsistency(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    // POST API to expose inference
    @PostMapping("/inference")
    public ResponseEntity<?> postInference(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
   
    @PostMapping("/unsatisfiable")
    public ResponseEntity<?> postUnsatisfaisableClasses(@RequestParam(required = false) MultipartFile ontologyFile,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) { 
    	int parametersCount = countParams(ontologyFile, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url,ontologyFile);
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
