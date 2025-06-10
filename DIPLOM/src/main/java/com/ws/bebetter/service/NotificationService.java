package com.ws.bebetter.service;

import com.ws.bebetter.entity.ChannelType;
import com.ws.bebetter.entity.NotificationType;

import java.util.List;
import java.util.UUID;

/**
 * Сервис генерации уведомлений.
 */
public interface NotificationService {

    /**
     * Генерирует уведомление в соответствии с указанным типом и каналом, отправляет его в очередь для обработки.
     *
     * @param channelTypes     содержащий информацию о каналах передачи уведомления.
     * @param notificationType содержащий тип уведомления.
     * @param userId           получатель уведомления.
     * @param content          содержимое уведомления.
     */
    <T> void processNotification(List<ChannelType> channelTypes, NotificationType notificationType,
                                 UUID userId, T content);

}
