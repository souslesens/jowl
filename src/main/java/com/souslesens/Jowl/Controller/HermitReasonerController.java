package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.model.reasonerInput;
import com.souslesens.Jowl.services.HermitReasonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    @PostMapping("/consistency")
    public ResponseEntity<?> postConsistency(@RequestBody(required = false) reasonerInput request) {
        // extract input parameters from the request object
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        byte[] ontologyContentDecoded64Bytes = null;
        String ontologyContentDecoded64 = null;
        if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
            ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
            ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
            System.out.println("Inference"+ontologyContentDecoded64);
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
                result = hermitReasonerService.getConsistency(filePath, url);
                // Here if we use filePath or Url
            }else {
                result = hermitReasonerService.getConsistency(ontologyContentDecoded64);
                // Here if we use the Encoded Content
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unsatisfiable")
    public ResponseEntity<?> postUnsatisfiableClasses(@RequestBody(required = false) reasonerInput request) {
        // extract input parameters from the request object
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
                result = hermitReasonerService.getUnsatisfaisableClasses(filePath, url);
                // Here if we use filePath or Url
            }else {
                result = hermitReasonerService.getUnsatisfaisableClasses(ontologyContentDecoded64);
                // Here if we use the Encoded Content
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inference")
    public ResponseEntity<?> postInferences(@RequestBody(required = false) reasonerInput request) {
        // extract input parameters from the request object
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
                result = hermitReasonerService.getInferences(filePath, url);
                // Here if we use filePath or Url
            }else {
                result = hermitReasonerService.getInferences(ontologyContentDecoded64);
                // Here if we use the Encoded Content
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
