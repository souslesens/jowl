package com.souslesens.Jowl;

import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JowlApplication {

	public static void main(String[] args) {
		System.getenv("SERVER_PORT");
		SpringApplication.run(JowlApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
		};
	}

}
