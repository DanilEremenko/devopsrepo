package com.ws.bebetter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalNotificationsListItemRs {

    private UUID id;

    private InternalNotificationAuthorDto author;

    private LocalDateTime creationTime;

    private String message;

    private Boolean isRead;

}

