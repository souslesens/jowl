package com.souslesens.Jowl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Value("${virtuoso.endpoint:}")
    private String virtuosoEndpoint;

    @Value("${virtuoso.user:}")
    private String virtuosoUser;

    @Value("${virtuoso.password:}")
    private String virtuosoPassword;

    public String getVirtuosoEndpoint() {
        return virtuosoEndpoint;
    }

    public String getVirtuosoUser() {
        return virtuosoUser;
    }

    public String getVirtuosoPassword() {
        return virtuosoPassword;
    }
}
