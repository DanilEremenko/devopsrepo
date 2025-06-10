package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.NotificationDto;
import com.ws.bebetter.entity.NotificationType;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.properties.MailProperties;
import com.ws.bebetter.service.EmailService;
import com.ws.bebetter.service.EmailTemplateService;
import com.ws.bebetter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;
    private final UserService userService;
    private final MailProperties mailProperties;

    @Override
    @RabbitListener(queues = "#{T(com.ws.bebetter.config.RabbitQueue).EMAIL_NOTIFICATION.getQueueName()}",
            messageConverter = "customJsonGenericMessageConverter")
    public <T> void processEmailNotification(NotificationDto<T> notificationDto) {
        String templateName = selectTemplate(notificationDto.getType());
        Map<String, Object> model = buildModel(notificationDto);
        String emailContent = emailTemplateService.buildEmailContent(templateName, model);
        sendEmail(notificationDto.getUserId(), emailContent, notificationDto.getSubject());
    }

    private String selectTemplate(NotificationType type) {
        return type.getTemplate();
    }

    private <T> Map<String, Object> buildModel(NotificationDto<T> notificationDto) {
        return notificationDto.getType().buildNotificationBody(notificationDto);
    }

    private void sendEmail(UUID userId, String emailContent, String subject) {
        User user = userService.getUserById(userId);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailProperties.getNotificationSender());
        message.setTo(user.getLogin());
        message.setSubject(subject);
        message.setText(emailContent);

        mailSender.send(message);
    }
}


