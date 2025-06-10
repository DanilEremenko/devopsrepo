package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRoleRs {
    private String login;

    private RoleType role;
}
