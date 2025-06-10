package com.ws.bebetter.web.controller;

import com.ws.bebetter.web.dto.InternalNotificationsListRq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Кейс проверки корректности данных запроса листа уведомлений пользователя.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsListValidationCase {

    /**
     * Запрос листа уведомлений пользователя.
     */
    private InternalNotificationsListRq internalNotificationsListRq;

    /**
     * Ожидаемый результат валидации
     */
    private boolean valid;

    /**
     * Комментарий. Почему именно ожидается такой результат.
     */
    private String comment;

}
