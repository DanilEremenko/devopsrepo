package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.FileRef;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserProfileDto {

    private UUID id;

    private FileRef photo;

    private LocalDate dateOfBirth;

    private String workExperience;

}
