package com.ws.bebetter.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class EditUserProfileRq {

    @NotNull(message = "Значение идентификатора не может быть нулевым.")
    private UUID userId;

    @NotNull(message = "Значение фамилии не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина фамилии должна составлять от 1 до 100 символов.")
    private String lastName;

    @NotNull(message = "Значение имени не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина имени должна составлять от 1 до 100 символов.")
    private String firstName;

    @Length(min = 1, max = 100, message = "Длина отчества должна составлять от 1 до 100 символов.")
    private String middleName;

    @Valid
    private Set<RoleType> roles;

    @ValidationUtil.EnumConstraint(enumClass = RoleType.class, message = "Неподдерживаемое значение роли.")
    private RoleType activeRole;

    private Boolean activeStatus;

    @NotNull(message = "Значение электронной почты не может быть пустым.")
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String login;

    @Valid
    private RefDto photo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    private String workExperience;
}