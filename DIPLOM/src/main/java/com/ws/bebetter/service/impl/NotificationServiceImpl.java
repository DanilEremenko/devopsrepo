package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.ChannelType;
import com.ws.bebetter.entity.NotificationDto;
import com.ws.bebetter.entity.NotificationType;
import com.ws.bebetter.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T> void processNotification(List<ChannelType> channelTypes, NotificationType notificationType,
                                        UUID userId, T content) {
        String subject = selectSubject(notificationType, content);
        channelTypes.forEach(channelType -> {
            String queueName = getQueueNameByChannelType(channelType);
            NotificationDto<T> notificationDto = NotificationDto.<T>builder()
                    .userId(userId)
                    .notificationParameters(content)
                    .type(notificationType)
                    .subject(subject)
                    .build();

            rabbitTemplate.convertAndSend(queueName, notificationDto);
        });
    }

    private String getQueueNameByChannelType(ChannelType channelType) {
        return channelType.getQueueName();
    }

    private <T> String selectSubject(NotificationType type, T content) {
        return type.buildNotificationSubject(type, content);
    }

}
