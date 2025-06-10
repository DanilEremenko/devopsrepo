package com.ws.bebetter.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserProfileDetailsInfoRs {
    private UUID id;

    private String lastName;

    private String firstName;

    private String middleName;

    private Set<UserRoleDto> roles;

    private UserRoleDto activeRole;

    private Boolean activeStatus;

    private String login;

    private RefDto photo;

    private LocalDate dateOfBirth;

    private String workExperience;

    private CompanyDto company;
}