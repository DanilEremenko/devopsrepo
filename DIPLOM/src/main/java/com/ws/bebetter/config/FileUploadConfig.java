package com.ws.bebetter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class FileUploadConfig {

    private String maxFileSize;

    private String maxRequestSize;

}
