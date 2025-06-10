package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.RegistrationRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса регистрации нового пользователя и компании.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterValidationCase {

    /**
     * Запрос регистрации нового пользователя и компании.
     */
    private RegistrationRq registrationRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
