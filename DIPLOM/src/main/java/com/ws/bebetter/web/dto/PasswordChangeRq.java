package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordChangeRq {

    @NotNull
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.PASSWORD,
            message = "Пароль должен содержать как минимум одну букву, одну цифру и один специальный символ.",
            min = 8, max = 50)
    private String oldPassword;

    @NotNull
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.PASSWORD,
            message = "Пароль должен содержать как минимум одну букву, одну цифру и один специальный символ.",
            min = 8, max = 50)
    private String newPassword;

}
