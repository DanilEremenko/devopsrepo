package com.ws.bebetter.config;

import lombok.Getter;

@Getter
public enum RabbitQueue {

    INTERNAL_NOTIFICATION("internal_notification"),
    EMAIL_NOTIFICATION("email_notification");

    private final String queueName;

    RabbitQueue(String queueName) {
        this.queueName = queueName;
    }

}
