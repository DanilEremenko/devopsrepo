package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.FeedbackSearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса списка отзывов специалиста
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackListValidationCase {

    /**
     * запрос
     */
    private Long userId;

    /**
     * Данные запроса списка отзывов
     */
    private FeedbackSearchRequest request;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
