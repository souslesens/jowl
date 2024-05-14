package com.souslesens.Jowl;

import com.souslesens.Jowl.config.AppConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

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

	private static void loadEnv() {
		Dotenv dotenv = Dotenv.configure().load();

	}

}
