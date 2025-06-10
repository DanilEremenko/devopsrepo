package com.ws.bebetter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class S3ServiceConfig {

    private String minioUser;

    private String minioPassword;

    private String minioEndpoint;

    private String minioBucketName;
}
