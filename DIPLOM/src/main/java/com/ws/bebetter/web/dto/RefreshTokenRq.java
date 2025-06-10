package com.ws.bebetter.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class RefreshTokenRq {

    @NonNull
    private String refreshToken;

}
