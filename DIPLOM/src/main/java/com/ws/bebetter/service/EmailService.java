package com.ws.bebetter.service;

import com.ws.bebetter.entity.NotificationDto;

/**
 * Сервис для обработки и отправки email уведомлений.
 */
public interface EmailService {

    /**
     * Получает уведомления из очереди и отправляет email сообщение.
     *
     * @param notificationDto данные для отправки сообщения.
     */
    <T> void processEmailNotification(NotificationDto<T> notificationDto);

}
