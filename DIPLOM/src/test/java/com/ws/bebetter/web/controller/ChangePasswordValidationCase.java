package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.PasswordChangeRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса на смену пароля.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordValidationCase {

    /**
     * Запрос на смену пароля.
     */
    private PasswordChangeRq passwordChangeRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;
}
