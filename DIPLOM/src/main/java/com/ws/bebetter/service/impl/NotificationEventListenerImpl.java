package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.ChannelType;
import com.ws.bebetter.entity.NotificationEvent;
import com.ws.bebetter.entity.NotificationType;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.NotificationService;
import com.ws.bebetter.web.dto.AddUserParamsDto;
import com.ws.bebetter.web.dto.FeedbackParamsDto;
import com.ws.bebetter.web.dto.BlockSpecialistParamsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListenerImpl {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(condition =
            "#event.notificationType == T(com.ws.bebetter.entity.NotificationType).REGISTER")
    public void handleRegistrationEvent(NotificationEvent<?> event) {
        User user = event.getUser();
        notificationService.processNotification(
                List.of(ChannelType.EMAIL),
                NotificationType.REGISTER,
                user.getId(),
                event.getNotificationParameters());
        log.info("Publishing registration event for user ID: {}", user.getId());
    }

    @Async
    @TransactionalEventListener(condition =
            "#event.notificationType == T(com.ws.bebetter.entity.NotificationType).PASSWORD_RECOVERY")
    public void handlePasswordRecoveryEvent(NotificationEvent<?> event) {
        User user = event.getUser();
        notificationService.processNotification(
                List.of(ChannelType.EMAIL),
                NotificationType.PASSWORD_RECOVERY,
                user.getId(),
                event.getNotificationParameters());
        log.info("Publishing password recovery event for user ID: {}", user.getId());

    }

    @Async
    @TransactionalEventListener(condition =
            "#event.notificationType == T(com.ws.bebetter.entity.NotificationType).FEEDBACK")
    public void handleCreationFeedbackEvent(NotificationEvent<?> event) {
        if (event.getNotificationParameters() instanceof FeedbackParamsDto feedbackParams) {
            User user = event.getUser();
            notificationService.processNotification(
                    List.of(ChannelType.EMAIL, ChannelType.INTERNAL),
                    NotificationType.FEEDBACK,
                    user.getId(),
                    event.getNotificationParameters());

            log.info("Publishing creation feedback event for specialist: {} {}",
                    feedbackParams.getSpecialist().getFirstName(),
                    feedbackParams.getSpecialist().getLastName());
        } else {
            throw new
                    IllegalArgumentException("Недопустимые параметры уведомления для типа обратной связи специалиста");
        }
    }

    @Async
    @TransactionalEventListener(condition =
            "#event.notificationType == T(com.ws.bebetter.entity.NotificationType).BLOCK_SPECIALIST")
    public void handleSpecialistBlockEvent(NotificationEvent<?> event) {
        if (event.getNotificationParameters() instanceof BlockSpecialistParamsDto blockSpecialistParams) {
            User user = event.getUser();
            notificationService.processNotification(
                    List.of(ChannelType.EMAIL, ChannelType.INTERNAL),
                    NotificationType.BLOCK_SPECIALIST,
                    user.getId(),
                    event.getNotificationParameters());
            log.info("Publishing a blocking event for a specialist: {} {}",
                    blockSpecialistParams.getSpecialist().getFirstName(),
                    blockSpecialistParams.getSpecialist().getLastName());
        } else {
            throw new IllegalArgumentException("Недопустимые параметры уведомления для типа блокировки специалиста");
        }
    }

    @Async
    @TransactionalEventListener(condition =
            "#event.notificationType == T(com.ws.bebetter.entity.NotificationType).ADD_NEW_USER")
    public void handleAddNeUserEvent(NotificationEvent<?> event) {
        User user = event.getUser();
        notificationService.processNotification(
                List.of(ChannelType.EMAIL),
                NotificationType.ADD_NEW_USER,
                user.getId(),
                event.getNotificationParameters()
        );
        if (event.getNotificationParameters() instanceof AddUserParamsDto addUserParamsDto) {
            log.info("Publishing add user event for by methodologist : {} {}",
                    addUserParamsDto.getMethodologist().getLastName(),
                    addUserParamsDto.getMethodologist().getFirstName());
        } else {
            throw new IllegalArgumentException("Недопустимые параметры уведомления " +
                    "для типа создания пользователя методологом");
        }
    }
}
