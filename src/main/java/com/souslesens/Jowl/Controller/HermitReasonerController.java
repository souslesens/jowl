package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.services.HermitReasonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hermit")
public class HermitReasonerController {

    @Autowired
    HermitReasonerService hermitReasonerService;

    @GetMapping(value = "/test")
    public ResponseEntity<String> pong()
    {

        return new ResponseEntity<String>("Test du serveur Spring: "+ HttpStatus.OK.name(), HttpStatus.OK);
    }

    @RequestMapping("/consistency")
    public ResponseEntity<String> getConsistency(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        int parametersCount = countNumberOfParametres( filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getConsistency(filePath, url), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/unsatisfiable")
    public ResponseEntity<String> getUnsatisfiableClasses(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        int parametersCount = countNumberOfParametres( filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getUnsatisfaisableClasses(filePath, url), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/inference")
    public ResponseEntity<String> getInferences(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        int parametersCount = countNumberOfParametres( filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getInferences(filePath, url), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


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
