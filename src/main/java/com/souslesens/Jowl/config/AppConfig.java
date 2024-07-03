package com.souslesens.Jowl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class AppConfig {
    private static final AppConfig instance = new AppConfig();

    private AppConfig() {
        virtuosoEndpoint = System.getenv("VIRTUOSO_ENDPOINT") != null ? System.getenv("VIRTUOSO_ENDPOINT") : null;
        virtuosoPassword = System.getenv("VIRTUOSO_PASSWORD") != null ? System.getenv("VIRTUOSO_PASSWORD") : null;
        virtuosoUser = System.getenv("VIRTUOSO_USER") != null ? System.getenv("VIRTUOSO_USER") : null;
    }

    @Bean
    public static AppConfig getInstance() {
        return instance;
    }
    @Value("${virtuoso.endpoint}")
    private final String virtuosoEndpoint;
    @Value("${virtuoso.user}")
    private final String virtuosoUser;
    @Value("${virtuoso.password}")
    private final String virtuosoPassword;

    public String getVirtuosoEndpoint() {
        return virtuosoEndpoint;
    }

    public String getVirtuosoUser() {
        if (virtuosoUser == null) {
            return "";
        }
        return virtuosoUser;
    }

    public String getVirtuosoPassword() {
        if (virtuosoPassword == null) {
            return "";
        }
        return virtuosoPassword;
    }
}