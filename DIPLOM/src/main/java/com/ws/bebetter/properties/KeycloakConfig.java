package com.ws.bebetter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "keycloak-initializer")
public class KeycloakConfig {

    private String url;

    private String masterRealm;

    private String applicationRealm;

    private String clientSecret;

    private String adminClientSecret;

    private String clientId;

    private String adminClientId;

    private String username;

    private String password;

}
