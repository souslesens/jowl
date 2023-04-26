package com.souslesens.Jowl.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.services.AlternativeReasonerService;
import com.souslesens.Jowl.services.ReasonerService;

@RestController
@RequestMapping("/alternative")
public class ReasonerAlternativeAPIController {

    @Autowired
    private AlternativeReasonerService reasonerServiceAlt;
    // POST API to expose consistency
    @PostMapping("/consistency")
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
                String result = reasonerServiceAlt.getConsistencyAlt(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    

    
    
    // POST API Alternative solution to expose inference
    @PostMapping("/inference")
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
                List<reasonerExtractTriples> result = reasonerServiceAlt.getInferencesAlt(filePath, url,ontologyFile);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
   
    @PostMapping("/unsatisfiable")
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
                String result = reasonerServiceAlt.getUnsatisfaisableClassesAlt(filePath, url,ontologyFile);
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
