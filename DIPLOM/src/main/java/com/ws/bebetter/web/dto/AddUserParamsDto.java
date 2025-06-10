package com.ws.bebetter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserParamsDto {
    private LoginRq user;

    private UserShortDto methodologist;

    private String link;
}