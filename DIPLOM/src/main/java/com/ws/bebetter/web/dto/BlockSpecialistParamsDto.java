package com.ws.bebetter.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockSpecialistParamsDto implements InternalNotificationParams {

    private UserShortDto specialist;

    private UserShortDto methodologist;

    private UserShortDto manager;

    @Override
    public UUID getRecipientId() {
        return manager.getId();
    }

    @Override
    public UUID getInitiatorId() {
        return methodologist.getId();
    }
}
