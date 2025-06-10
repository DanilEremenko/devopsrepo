package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.PasswordRecoveryRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса на восстановление пароля.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryValidationCase {

    /**
     * Запрос на восстановление пароля.
     */
    private PasswordRecoveryRq passwordRecoveryRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
