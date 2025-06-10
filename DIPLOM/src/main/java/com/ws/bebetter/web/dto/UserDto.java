package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;

    @NotNull(message = "Значение фамилии не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина фамилии должна составлять от 1 до 100 символов.")
    private String lastname;

    @NotNull(message = "Значение имени не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина имени должна составлять от 1 до 100 символов.")
    private String firstname;

    @Length(min = 1, max = 100, message = "Длина отчества должна составлять от 1 до 100 символов.")
    private String middlename;

    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String login;

    @EqualsAndHashCode.Exclude
    private Set<UserRoleDto> userRoles;

}


