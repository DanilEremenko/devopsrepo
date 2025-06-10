package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetRq {

    @NotNull
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String login;

    @NotNull
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.UUID,
            message = "Неправильный формат UUID.")
    private String secret;

    @NotNull
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.PASSWORD,
            message = "Пароль должен содержать как минимум одну букву, одну цифру и один специальный символ.",
            min = 8, max = 50)
    private String newPassword;

}
