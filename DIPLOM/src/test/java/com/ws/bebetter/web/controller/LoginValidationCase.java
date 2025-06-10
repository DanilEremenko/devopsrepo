package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.LoginRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса аутентификации пользователя.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginValidationCase {

    /**
     * Запрос аутентификации пользователя.
     */
    private LoginRq loginRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
