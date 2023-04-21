package com.souslesens.Jowl.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<?> getUnsatisfaisableClasses(@RequestParam String filePath,
    		@RequestParam String operation) {
    	if (operation.equalsIgnoreCase("unsatisfaisability")) {
        try {
            String result = reasonerService.getUnsatisfaisableClasses(filePath, operation);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    	}return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @GetMapping("/consistency")
    public ResponseEntity<?> getConsistency(@RequestParam String filePath,
    		@RequestParam String operation) {
    	if (operation.equalsIgnoreCase("consistency")) {
        try {
            String result = reasonerService.getConsistency(filePath, operation);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        
    	}return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @GetMapping("/unference")
    public ResponseEntity<?> getInference(@RequestParam String filePath,
    		@RequestParam String operation) {
    	if (operation.equalsIgnoreCase("computeinference")) {
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, operation);
                
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }	
    	}return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    }
}
