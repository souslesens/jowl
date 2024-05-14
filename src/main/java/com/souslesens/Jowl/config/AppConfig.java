package com.souslesens.Jowl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class AppConfig {
    private static final AppConfig instance = new AppConfig();

    private AppConfig() {
        serverPort = Integer.parseInt(System.getenv("SERVER_PORT"));
        virtuosoEndpoint = System.getenv("VIRTUOSO_ENDPOINT");
        virtuosoPassword = System.getenv("VIRTUOSO_PASSWORD");
    }

    @Bean
    public static AppConfig getInstance() {
        return instance;
    }
    private int serverPort;

    private String virtuosoEndpoint;

    private String virtuosoPassword;

    public int getServerPort() {
        return serverPort;
    }

    public String getVirtuosoEndpoint() {
        return virtuosoEndpoint;
    }

    public String getVirtuosoPassword() {
        return virtuosoPassword;
    }

}