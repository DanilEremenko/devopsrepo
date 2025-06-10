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
public class FeedbackParamsDto implements InternalNotificationParams {

    private UserShortDto specialist;

    private UserShortDto managerInitiator;

    private UserShortDto managerRecipient;

    @Override
    public UUID getRecipientId() {
        return managerRecipient.getId();
    }

    @Override
    public UUID getInitiatorId() {
        return managerInitiator.getId();
    }

}
