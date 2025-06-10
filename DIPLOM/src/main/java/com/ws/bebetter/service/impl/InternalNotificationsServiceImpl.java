package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.*;
import com.ws.bebetter.repository.InternalNotificationRepository;
import com.ws.bebetter.service.InternalNotificationsService;
import com.ws.bebetter.service.UserProfileService;
import com.ws.bebetter.service.UserService;
import com.ws.bebetter.util.EntityProxyResolver;
import com.ws.bebetter.util.TimeUtil;
import com.ws.bebetter.web.dto.InternalNotificationAuthorDto;
import com.ws.bebetter.web.dto.InternalNotificationParams;
import com.ws.bebetter.web.dto.InternalNotificationsListItemRs;
import com.ws.bebetter.web.dto.InternalNotificationsListRq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternalNotificationsServiceImpl implements InternalNotificationsService {

    private final EntityProxyResolver proxyResolver;
    private final InternalNotificationRepository internalNotificationRepository;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final TimeUtil timeUtil;

    @Override
    @RabbitListener(queues = "#{T(com.ws.bebetter.config.RabbitQueue).INTERNAL_NOTIFICATION.getQueueName()}",
            messageConverter = "customJsonGenericMessageConverter")
    public <T> void accumulateInternalNotifications(NotificationDto<T> notificationDto) {
        if (notificationDto.getNotificationParameters() instanceof InternalNotificationParams params) {
            User initiator = proxyResolver.getReference(User.class, params.getInitiatorId());
            User recipient = proxyResolver.getReference(User.class, params.getRecipientId());

            InternalNotification internalNotification = InternalNotification.builder()
                    .initiator(initiator)
                    .recipient(recipient)
                    .creationTime(timeUtil.getCurrentDateTime())
                    .message(notificationDto.getSubject())
                    .build();

            internalNotificationRepository.save(internalNotification);
            log.info("Processed notification for recipient with id: {} from initiator: {}",
                    params.getRecipientId(), params.getInitiatorId());
        } else {
            throw new IllegalArgumentException("Неподдерживаемые параметры уведомления");
        }

    }

    @Override
    @Transactional
    public Page<InternalNotificationsListItemRs> getUserNotificationsList(
            InternalNotificationsListRq internalNotificationsListRq, User currentUser) {
        if (!internalNotificationRepository.existsByRecipient(currentUser)) return Page.empty();

        Sort sort = Sort.by(Sort.Direction.DESC, InternalNotification_.CREATION_TIME);
        Pageable pageable = PageRequest.of(internalNotificationsListRq.getPageNumber() - 1,
                internalNotificationsListRq.getCountPerPage(), sort);

        Page<InternalNotification> notifications = internalNotificationRepository.findAll(
                (root, query, cb) -> cb.equal(root.get(InternalNotification_.RECIPIENT), currentUser), pageable
        );

        Page<InternalNotificationsListItemRs> internalNotificationsListItemRsPage = notifications
                .map(internalNotification -> {
                    User initiator = userService.getUserById(internalNotification.getInitiator().getId());

                    UserProfile userProfile = userProfileService.getUserProfileByUser(initiator);
                    String authorPhoto = null;
                    if (userProfile != null && userProfile.getPhoto() != null) {
                        authorPhoto = userProfile.getPhoto().getFileName();
                    }

                    return InternalNotificationsListItemRs.builder()
                            .id(internalNotification.getId())
                            .author(InternalNotificationAuthorDto.builder()
                                    .userId(initiator.getId())
                                    .fullName(initiator.getFullName())
                                    .email(initiator.getLogin())
                                    .photoPath(authorPhoto)
                                    .build())
                            .creationTime(internalNotification.getCreationTime())
                            .message(internalNotification.getMessage())
                            .isRead(internalNotification.getIsRead())
                            .build();
                });

        setAllReceivedNotificationIsRead(currentUser);
        return internalNotificationsListItemRsPage;

    }

    private void setAllReceivedNotificationIsRead(User currentUser) {
        internalNotificationRepository.markAllAsReadByUserWithReadTime(currentUser, timeUtil.getCurrentDateTime());
    }

    @Override
    public Long getCountOfUnreadNotifications(User currentUser) {
        return internalNotificationRepository.count(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get(InternalNotification_.RECIPIENT), currentUser),
                        cb.isFalse(root.get(InternalNotification_.IS_READ))
                )
        );
    }

}
