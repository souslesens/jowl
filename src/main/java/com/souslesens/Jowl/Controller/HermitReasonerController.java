package com.souslesens.Jowl.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souslesens.Jowl.model.parametresInputInference;
import com.souslesens.Jowl.model.reasonerInference;
import com.souslesens.Jowl.model.reasonerInput;
import com.souslesens.Jowl.services.HermitReasonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping({"/reasoner", "/hermit"})
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
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String graphName
            ) {
        int parametersCount = countNumberOfParametres( filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getConsistency(filePath, url, graphName), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/unsatisfiable")
    public ResponseEntity<String> getUnsatisfiableClasses(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String graphName) {
        int parametersCount = countNumberOfParametres( filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getUnsatisfaisableClasses(filePath, url, graphName ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/inference")
    public ResponseEntity<String> getInferences(
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String graphName) {
        int parametersCount = countNumberOfParametres( filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            return new ResponseEntity<String>(hermitReasonerService.getInferences(filePath, url, Collections.singletonList("All"), graphName), HttpStatus.OK);
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
        String graphName = request.getGraphName();
        byte[] ontologyContentDecoded64Bytes = null;
        String ontologyContentDecoded64 = null;
        if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
            ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
            ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
            System.out.println("Inference"+ontologyContentDecoded64);
        }
        int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            String result ;
            if (!(filePath == null) || !(url == null) || !(graphName == null ) ) {
                result = hermitReasonerService.getConsistency(filePath, url, graphName);
                // Here if we use filePath or Url
            }else {
                result = hermitReasonerService.getConsistency(ontologyContentDecoded64);
                // Here if we use the Encoded Content
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unsatisfiable")
    public ResponseEntity<?> postUnsatisfiableClasses(@RequestBody(required = false) reasonerInput request) {
        // extract input parameters from the request object
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        String graphName = request.getGraphName();
        byte[] ontologyContentDecoded64Bytes = null;
        String ontologyContentDecoded64 = null;
        if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
            ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
            ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
        }
        int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            String result ;
            if (!(filePath == null) || !(url == null) || !(graphName == null ) ) {
                result = hermitReasonerService.getUnsatisfaisableClasses(filePath, url, graphName);
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
    public ResponseEntity<?> postInferences(@RequestBody(required = false) reasonerInference request) {
        // extract input parameters from the request object
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        String[] reqParametres = request.getParams();
        String graphName = request.getGraphName();
        byte[] ontologyContentDecoded64Bytes = null;
        String ontologyContentDecoded64 = null;
        if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
            ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
            ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
        }

        List<String> valuesList = new ArrayList<>();
        List<String> valuesList2 = new ArrayList<>();
        for (int i = 0; i < reqParametres.length; i++) {
            if (reqParametres[i].equals("All_OWL")) {
                valuesList2.add("All");
                break;
            }

        }
        if (valuesList2.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> jsonMappin = hermitReasonerService.getParameteresInferenceMethod();


                // Loop through the JSON data
                for (Map.Entry<String, String> entry : jsonMappin.entrySet()) {
                    String key = entry.getKey();
                    for (int i = 0; i < reqParametres.length; i++) {
                        if (reqParametres[i].equals(key)) {
                            valuesList.add(entry.getValue());
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else {
            valuesList = valuesList2;
        }

        int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url, graphName);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
        try {
            String result ;
            if (!(filePath == null) || !(url == null) || !(graphName == null ) ) {
                result = hermitReasonerService.getInferences(filePath, url, valuesList, graphName );
                // Here if we use filePath or Url
            }else {
                result = hermitReasonerService.getInferences(ontologyContentDecoded64, valuesList);
                // Here if we use the Encoded Content
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Post API For STRING
    @GetMapping("/parametres")
    public ResponseEntity<?> retrieveParameteresInferenceMethod() {
        Map<String, String> hashMap = new HashMap<>();
        // Add key-value pairs to the HashMap
        hashMap.put("equivalentClass", "CustomInferredEquivalentClassesAxiomGenerator()"); // Covers owl:equivalentClass Inferences
        hashMap.put("sameIndividual", "CustomSameIndividualAxiomGenerator()"); // Covers Owl SameAs
        hashMap.put("IntersectionOf", "CustomInferredIntersectionOfAxiomGenerator()"); // Covers Owl:Intersection:Of Inferences
        hashMap.put("UnionOf", "CustomInferredUnionOfAxiomGenerator()"); // Covers Owl:UnionOf inferences
        hashMap.put("DisjointClasses", "CustomInferredDisjointClassesAxiomGenerator()"); // Covers Owl:DisjointClasses Inferences
        hashMap.put("differentIndividual","CustomInferredDifferentIndividualAxiomGenerator()" );
        hashMap.put("HasValue", "CustomInferredHasValueAxiomGenerator()"); // Covers owl:hasValue restriction
        hashMap.put("InverseObjectProperties", "CustomInferredInverseObjectPropertiesAxiomGenerator()"); // Covers Inveerse object properties // if property A relates individual x to individual y, then property B relates individual y to individual x
        hashMap.put("AllValuesFrom", "CustomInferredAllValuesFromAxiomGenerator()"); // Cover Owl:allValuesFrom
        hashMap.put("SomeValuesFrom", "CustomInferredSameValueSomeValuesFromAxiomGenerator()"); // Cover Owl:someValuesFrom
        hashMap.put("DomainAndRange", "CustomInferredDomainAndRangeAxiomGenerator()"); // Cover Domain and Range
        hashMap.put("ClassAssertion", "InferredClassAssertionAxiomGenerator()");
        hashMap.put("SubClass", "InferredSubClassAxiomGenerator()"); // Covers Rdfs:SubClass
        hashMap.put("DataPropertyCharacteristic", "InferredDataPropertyCharacteristicAxiomGenerator()");
        hashMap.put("EquivalentDataProperty", "InferredEquivalentDataPropertiesAxiomGenerator()");
        hashMap.put("EquivalentObjectProperty", "InferredEquivalentObjectPropertyAxiomGenerator()");
        hashMap.put("SubObjectProperty", "InferredSubObjectPropertyAxiomGenerator()");
        hashMap.put("SubDataPropertyOfAxiom", "InferredSubDataPropertyAxiomGenerator()");
        hashMap.put("ObjectPropertyCharacteristic", "InferredObjectPropertyCharacteristicAxiomGenerator()"); // FunctionalObjectProperty/InverseFunctionalObjectProperty/SymmetricObjectProperty/AsymmetricObjectProperty/ReflexiveObjectProperty/IrreflexiveObjectProperty/TransitiveObjectProperty
        hashMap.put("SubDataPropertyOfAxiom", "InferredPropertyAssertionGenerator()"); // This Covers both OWLObjectPropertyAssertionAxiom and OWLDataPropertyAssertionAxiom
        hashMap.put("ComplementOf", "CustomInferredComplementOfAxiomGenerator()"); // Cover OwlComplementOf
        hashMap.put("All_OWL", "All"); // Covers Evreything
        try {


            return ResponseEntity.ok(hashMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //Post API For STRING To use Parametere AS boolean
    @PostMapping("/parametres")
    public ResponseEntity<?> retrieveParameteresInference(@RequestBody(required = false) parametresInputInference request) {

        Boolean equivalentClass = request.getEquivalentClass();
        Boolean sameIndividual = request.getSameIndividual();
        Boolean IntersectionOf = request.getIntersectionOf();
        Boolean UnionOf = request.getUnionOf();
        Boolean DisjointClasses  = request.getDisjointClasses();
        Boolean differentIndividual = request.getDifferentIndividual();
        Boolean HasValue = request.getHasValue();
        Boolean InverseObjectProperties = request.getInverseObjectProperties();
        Boolean AllValuesFrom = request.getAllValuesFrom();
        Boolean SomeValuesFrom = request.getSomeValuesFrom();
        Boolean DomainAndRange  = request.getDomainAndRange();

        int parametersCount = countNumberOfParametres(equivalentClass, sameIndividual, IntersectionOf,UnionOf,DisjointClasses,differentIndividual,HasValue,InverseObjectProperties,AllValuesFrom,SomeValuesFrom,DomainAndRange);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 11) {
            return ResponseEntity.badRequest().body("paramateres u provided are more than u should pass");
        }
        try {

            List<parametresInputInference> parametresInference = new LinkedList<>();
            parametresInference.add(new parametresInputInference(equivalentClass, sameIndividual, IntersectionOf,UnionOf,DisjointClasses,differentIndividual,HasValue,InverseObjectProperties,AllValuesFrom,SomeValuesFrom,DomainAndRange));
            return ResponseEntity.ok(parametresInference);
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
