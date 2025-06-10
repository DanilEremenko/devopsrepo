package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.LogoutRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса выхода из системы аутентифицированным пользователем.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutValidationCase {

    /**
     * Запрос выхода из системы.
     */
    private LogoutRq logoutRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
