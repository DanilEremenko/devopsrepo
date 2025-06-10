package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
public class AddUserRq {
    @NotNull(message = "Значение фамилии не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина фамилии должна составлять от 1 до 100 символов.")
    private String lastName;

    @NotNull(message = "Значение имени не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина имени должна составлять от 1 до 100 символов.")
    private String firstName;

    @Length(min = 1, max = 100, message = "Длина отчества должна составлять от 1 до 100 символов.")
    private String middleName;

    @Length(min = 5, max = 50, message = "Длина логина должна составлять от 10 до 50 символов.")
    @Email
    private String login;

    @NotNull
    private Boolean activeStatus;

    @NotNull
    @Valid
    private Set<RoleType> roles;
}