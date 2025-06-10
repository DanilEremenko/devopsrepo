package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.AddUserRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewUserValidationCase {

    /**
     * Данные запроса
     */
    private AddUserRq addUserRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;
}
