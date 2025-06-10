package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.PasswordResetRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса на установку нового пароля.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetValidationCase {

    /**
     * Запрос на установку пароля.
     */
    private PasswordResetRq passwordResetRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
