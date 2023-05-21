package com.souslesens.Jowl.Controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souslesens.Jowl.model.parametresInputInference;
import com.souslesens.Jowl.model.reasonerInference;
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
                String result = reasonerService.getInferences(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("ERROR");
            }
            
        	
        
    }
    //Post API For STRING
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
    	int parametersCount = countParams(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result ;
            	if (!(filePath == null) || !(url == null) ) {
                result = reasonerService.postConsistency(filePath, url);
                System.out.println("here");
            	}else {
            	result = reasonerService.postConsistencyContent(ontologyContentDecoded64);
            	System.out.println("here2");
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    //Post API For STRING
    @PostMapping("/inference")
    public ResponseEntity<?> postInference(@RequestBody(required = false) reasonerInference request) { 
    	RestTemplate restTemplate = new RestTemplate();
        // extract input parameters from the request object
        String filePath = request.getFilePath();
        String url = request.getUrl();
        String ontologyContentEncoded64 = request.getOntologyContentEncoded64();
        String[] reqParams = request.getParams();

    	byte[] ontologyContentDecoded64Bytes = null;
    	String ontologyContentDecoded64 = null;
    	if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
    	 ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
    	 ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
    	}
        // Configure the HTTP request headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
    	 String externalApiUrl = "http://localhost:8080/reasoner/parametres";
    	 ResponseEntity<String> response = restTemplate.exchange(externalApiUrl, HttpMethod.GET, entity, String.class);
    	 System.out.println(response.getBody());
    	 List<String> valuesList = new ArrayList<>();
    	 List<String> valuesList2 = new ArrayList<>();
	     for (int i = 0; i < reqParams.length; i++) {
	        	 if (reqParams[i].equals("All_OWL")) {
	                valuesList2.add("All");
	                System.out.println("this is the end");
	                break;
	            }
	        	
	        }
    	 if (valuesList2.isEmpty()) {
    		 System.out.println("This is the beginning");
        	 try {
     		    ObjectMapper objectMapper = new ObjectMapper();
     		    Map<String, String> jsonMap = objectMapper.readValue(response.getBody(), Map.class);
     		    

     		    // Loop through the JSON data
     		    for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
     		        String key = entry.getKey();
     		        System.out.println("Key: " + key);
     		        System.out.println("Value: " + entry.getValue());

     		        
     		        for (int i = 0; i < reqParams.length; i++) {
     		        	 if (reqParams[i].equals(key)) {
     		                valuesList.add(entry.getValue());
     		                break;
     		            }
     		        }
     		    }

     		} catch (Exception e) {
     			return ResponseEntity.badRequest().body("Error");
          }
    	 }else {
    		 valuesList = valuesList2;
    	 }


    	int parametersCount = countParams(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result ;
            	if (!(filePath == null) || !(url == null) ) {
                result = reasonerService.postInferences(filePath, url,valuesList);
            	}else {
            	result = reasonerService.postInferencesContent(ontologyContentDecoded64,valuesList);
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
            }
        }
    //Post API For STRING
    @PostMapping("/unsatisfiable")
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
    	int parametersCount = countParams(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result ;
            	if (!(filePath == null) || !(url == null) ) {
                result = reasonerService.postUnsatisfaisableClasses(filePath, url);
            	}else {
            	result = reasonerService.postUnsatisfaisableClassesContent(ontologyContentDecoded64);
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
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
        hashMap.put("HasValue", "InferredHasValueAxiomGenerator()");
        hashMap.put("InverseObjectProperties", "InferredInverseObjectPropertiesAxiomGenerator()"); //  if property A relates individual x to individual y, then property B relates individual y to individual x
        hashMap.put("AllValuesFrom", "InferredAllValuesFromAxiomGenerator()");
        hashMap.put("SomeValuesFrom", "InferredSameValueSomeValuesFromAxiomGenerator()");
        hashMap.put("DomainAndRange", "InferredDomainAndRangeAxiomGenerator()");
        hashMap.put("ClassAssertion", "InferredClassAssertionAxiomGenerator()");
        hashMap.put("SubClass", "InferredSubClassAxiomGenerator()");
        hashMap.put("DataPropertyCharacteristic", "InferredDataPropertyCharacteristicAxiomGenerator()");
        hashMap.put("EquivalentDataProperty", "InferredEquivalentDataPropertiesAxiomGenerator()");
        hashMap.put("EquivalentObjectProperty", "InferredEquivalentObjectPropertyAxiomGenerator()");
        hashMap.put("SubObjectProperty", "InferredSubObjectPropertyAxiomGenerator()");
        hashMap.put("SubDataPropertyOfAxiom", "InferredSubDataPropertyAxiomGenerator()");
        hashMap.put("ObjectPropertyCharacteristic", "InferredObjectPropertyCharacteristicAxiomGenerator()"); // FunctionalObjectProperty/InverseFunctionalObjectProperty/SymmetricObjectProperty/AsymmetricObjectProperty/ReflexiveObjectProperty/IrreflexiveObjectProperty/TransitiveObjectProperty 
        hashMap.put("SubDataPropertyOfAxiom", "InferredPropertyAssertionGenerator()"); // This Covers both OWLObjectPropertyAssertionAxiom and OWLDataPropertyAssertionAxiom
        hashMap.put("All_OWL", "All");
            try {

            	
                return ResponseEntity.ok(hashMap);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error");
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
        
    	int parametersCount = countParams(equivalentClass, sameIndividual, IntersectionOf,UnionOf,DisjointClasses,differentIndividual,HasValue,InverseObjectProperties,AllValuesFrom,SomeValuesFrom,DomainAndRange);
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
