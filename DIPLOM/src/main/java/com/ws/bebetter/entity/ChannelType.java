package com.ws.bebetter.entity;

import com.ws.bebetter.config.RabbitQueue;

public enum ChannelType {
    INTERNAL(RabbitQueue.INTERNAL_NOTIFICATION),
    EMAIL(RabbitQueue.EMAIL_NOTIFICATION);

    private final RabbitQueue rabbitQueue;

    ChannelType(RabbitQueue rabbitQueue) {
        this.rabbitQueue = rabbitQueue;
    }

    public String getQueueName() {
        return rabbitQueue.getQueueName();
    }
}
