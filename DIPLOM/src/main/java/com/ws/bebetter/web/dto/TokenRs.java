package com.ws.bebetter.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRs {

    private String accessToken;

    private String refreshToken;

}
