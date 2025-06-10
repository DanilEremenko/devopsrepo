package com.ws.bebetter.config;

import com.ws.bebetter.properties.KeycloakConfig;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.app.VelocityEngine;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ApplicationConfig {

    private final KeycloakConfig keycloakConfig;
    private final S3ServiceConfig s3ServiceConfig;

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(props);
        return velocityEngine;
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.PASSWORD)
                .realm(keycloakConfig.getMasterRealm())
                .clientId(keycloakConfig.getAdminClientId())
                .clientSecret(keycloakConfig.getAdminClientSecret())
                .username(keycloakConfig.getUsername())
                .password(keycloakConfig.getPassword())
                .serverUrl(keycloakConfig.getUrl())
                .build();
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(s3ServiceConfig.getMinioEndpoint())
                .credentials(s3ServiceConfig.getMinioUser(), s3ServiceConfig.getMinioPassword())
                .build();
    }

}

