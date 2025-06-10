package com.ws.bebetter.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class LogoutRq {

    @NonNull
    private String refreshToken;

}
