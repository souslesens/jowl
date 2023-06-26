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
    	int parametersCount = countNumberOfParametres( filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
                String result = reasonerService.getUnsatisfaisableClasses(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    @GetMapping("/consistency")
    public ResponseEntity<?> getConsistency(@RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        
    		int parametersCount = countNumberOfParametres( filePath, url);
        	if (parametersCount == 0) {
            	return ResponseEntity.badRequest().body("At least one of params should be provided");
        	} else if (parametersCount > 1) {
            	return ResponseEntity.badRequest().body("Only one of params should be provided");
        	}
            try {
                String result = reasonerService.getConsistency(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            
        	
        
    }
    @GetMapping("/inference")
    public ResponseEntity<?> getInference(@RequestParam(required = false) String filePath,
            @RequestParam(required = false) String url) {
        
    		int parametersCount = countNumberOfParametres( filePath, url);
        	if (parametersCount == 0) {
            	return ResponseEntity.badRequest().body("At least one of params should be provided");
        	} else if (parametersCount > 1) {
            	return ResponseEntity.badRequest().body("Only one of params should be provided");
        	}
            try {
                String result = reasonerService.getInferences(filePath, url);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
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
    	int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url);
        if (parametersCount == 0) {
            return ResponseEntity.badRequest().body("At least one of params should be provided");
        } else if (parametersCount > 1) {
            return ResponseEntity.badRequest().body("Only one of params should be provided");
        }
            try {
            	String result ;
            	if (!(filePath == null) || !(url == null) ) {
                result = reasonerService.postConsistency(filePath, url);
                // Here if we use filePath or Url
            	}else {
            	result = reasonerService.postConsistencyContent(ontologyContentDecoded64);
            	// Here if we use the Encoded Content
            	}
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
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
        String[] reqParametres = request.getParams();
    	byte[] ontologyContentDecoded64Bytes = null;
    	String ontologyContentDecoded64 = null;
    	if (ontologyContentEncoded64 != null && !ontologyContentEncoded64.isEmpty()) {
    	 ontologyContentDecoded64Bytes = Base64.getMimeDecoder().decode(ontologyContentEncoded64);
    	 ontologyContentDecoded64 = new String(ontologyContentDecoded64Bytes, StandardCharsets.UTF_8);
    	}
    	// Automatic call to the parametres API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
    	 String externalApiUrl = "http://localhost:9170/reasoner/parametres";
    	 ResponseEntity<String> response = restTemplate.exchange(externalApiUrl, HttpMethod.GET, entity, String.class);
    	 System.out.println(response.getBody());
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
     		    Map<String, String> jsonMappin = objectMapper.readValue(response.getBody(), Map.class);
     		    

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


    	int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url);
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
                return ResponseEntity.badRequest().body(e.getMessage());
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
    	int parametersCount = countNumberOfParametres(ontologyContentDecoded64, filePath, url);
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
