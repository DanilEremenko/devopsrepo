package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.FeedbackDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных отзыва
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackValidationCase {

    /**
     * запрос
     */
    private Long userId;

    /**
     * Данные отзыва
     */
    private FeedbackDto data;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;
}