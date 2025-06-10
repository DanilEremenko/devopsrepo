package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.SetActiveRoleRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileValidationCase {

    /**
     * Данные роли
     */

    private SetActiveRoleRq data;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
