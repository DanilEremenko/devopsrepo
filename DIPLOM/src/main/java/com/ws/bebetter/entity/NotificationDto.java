package com.ws.bebetter.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto<T> {

    private UUID userId;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private T notificationParameters;

    private NotificationType type;

    private String subject;
}
