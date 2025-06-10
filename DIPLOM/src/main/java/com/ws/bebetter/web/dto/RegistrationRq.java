package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegistrationRq {

    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String email;

    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.PASSWORD,
            message = "Пароль должен соответствовать формату и содержать от 8 до 50 символов.",
            min = 8,
            max = 50)
    private String password;

    @NotNull(message = "Значение имени не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина имени должна составлять от 1 до 100 символов.")
    private String firstName;

    @NotNull(message = "Значение фамилии не может быть пустым.")
    @Length(min = 1, max = 100, message = "Длина фамилии должна составлять от 1 до 100 символов.")
    private String lastName;

    @Length(min = 1, max = 100, message = "Длина отчества должна составлять от 1 до 100 символов.")
    private String middleName;

    @Valid
    @NotNull
    private CompanyDto company;

}
