package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordRecoveryRq {

    @NotNull(message = "Значение электронной почты не может быть пустым.")
    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String login;

}
