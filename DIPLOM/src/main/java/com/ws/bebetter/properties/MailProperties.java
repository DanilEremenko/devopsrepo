package com.ws.bebetter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    private String domain;

    private String notificationSender;

}
