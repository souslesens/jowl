package com.souslesens.Jowl.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jena")
public class Jena { 
    @PostMapping("/rdf2triple")
    public ResponseEntity<?> postUnsatisfaisableClassesAlt(@RequestParam(required = false) String url){

            return ResponseEntity.badRequest().body("Only one of params should be provided");}



}
