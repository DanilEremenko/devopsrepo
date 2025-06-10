package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.EditUserProfileRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditUserProfileByMethodologistValidationCase {
    /**
     * Запрос на обновление профиля методологом
     */
    private EditUserProfileRq editUserProfileRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;
}
