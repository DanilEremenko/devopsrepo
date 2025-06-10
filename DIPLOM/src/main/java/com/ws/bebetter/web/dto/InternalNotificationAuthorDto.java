package com.ws.bebetter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalNotificationAuthorDto {

    private UUID userId;

    private String photoPath;

    private String fullName;

    private String email;

}
