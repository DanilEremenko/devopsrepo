package com.ws.bebetter.web.dto;

import com.ws.bebetter.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRq {

    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.EMAIL,
            message = "Электронная почта должна соответствовать формату и содержать не более 50 символов.",
            max = 50)
    private String login;

    @ValidationUtil.ValidString(
            type = ValidationUtil.ValidString.Type.PASSWORD,
            message = "Пароль должен соответствовать формату и содержать от 8 до 50 символов.",
            min = 8,
            max = 50)
    private String password;

}
