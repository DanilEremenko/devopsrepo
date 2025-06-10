package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.UserListRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса списка пользователей
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserListValidationCase {

    /**
     * Данные запроса списка пользователей
     */
    private UserListRequest request;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;
}
