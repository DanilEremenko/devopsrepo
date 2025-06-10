package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.RefreshTokenRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса на обновление пары токенов.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenValidationCase {

    /**
     * Запрос на обновление пары токенов.
     */
    private RefreshTokenRq refreshTokenRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
