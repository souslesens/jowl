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
    		@RequestParam String url) {
        if (url != null && !url.isEmpty() && filePath == null) {
            try {
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (url == null && !filePath.isEmpty() && filePath != null) {
            try {
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/consistency")
    public ResponseEntity<?> getConsistency(@RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        if (url != null && !url.isEmpty() && filePath == null) {
            try {
                String result = reasonerService.getConsistency(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (url == null && !filePath.isEmpty() && filePath != null) {
            try {
                String result = reasonerService.getConsistency(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/inference")
    public ResponseEntity<?> getInference(@RequestParam(required = false) String filePath,
                                           @RequestParam(required = false) String url) {
        if (url != null && !url.isEmpty() && filePath == null) {
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (url == null && !filePath.isEmpty() && filePath != null) {
            try {
                List<reasonerExtractTriples> result = reasonerService.getInferences(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
