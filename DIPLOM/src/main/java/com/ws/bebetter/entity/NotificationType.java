package com.ws.bebetter.entity;

import com.ws.bebetter.web.dto.AddUserParamsDto;
import com.ws.bebetter.web.dto.FeedbackParamsDto;
import com.ws.bebetter.web.dto.BlockSpecialistParamsDto;
import lombok.Getter;

import java.util.Map;

@Getter
public enum NotificationType {

    REGISTER("TEMP-EM-AUTH1-USER-REGISTRATION.vm"),
    PASSWORD_RECOVERY("TEMP-EM-AUTH2-RESTORE-PASSWORD.vm"),
    FEEDBACK("TEMP-EM-MNG3-FEEDBACK-EMAIL-NOTIFICATION.vm"),
    ADD_NEW_USER("TEMP-EM-AUTH3-ADD-USER-BY-METHODOLOGIST.vm"),
    BLOCK_SPECIALIST("TEMP-EM-MNG3-BLOCK_SPECIALIST-EMAIL-NOTIFICATION.vm");

    private final String template;

    NotificationType(String template) {
        this.template = template;
    }

    public <T> Map<String, Object> buildNotificationBody(NotificationDto<T> notificationDto) {
        return switch (notificationDto.getType()) {
            case REGISTER -> Map.of("link", notificationDto.getNotificationParameters() == null ?
                    "" : notificationDto.getNotificationParameters());
            case PASSWORD_RECOVERY -> Map.of("link", notificationDto.getNotificationParameters() == null ?
                    "" : notificationDto.getNotificationParameters());
            case FEEDBACK -> {
                if (notificationDto.getNotificationParameters() instanceof FeedbackParamsDto feedbackParams) {
                    yield Map.of(
                            "manager_first_name", feedbackParams.getManagerInitiator().getFirstName(),
                            "manager_last_name", feedbackParams.getManagerInitiator().getLastName(),
                            "specialist_first_name", feedbackParams.getSpecialist().getFirstName(),
                            "specialist_last_name", feedbackParams.getSpecialist().getLastName(),
                            "profession", "feedbackParams.getSpecialist().getProfession()"
                    );
                } else {
                    yield Map.of();
                }
            }
            case ADD_NEW_USER -> {
                if (notificationDto.getNotificationParameters() instanceof AddUserParamsDto addUserParamsDto) {
                    yield Map.of(
                            "methodologist_first_name", addUserParamsDto.getMethodologist().getFirstName(),
                            "methodologist_last_name", addUserParamsDto.getMethodologist().getLastName(),
                            "new_user_login", addUserParamsDto.getUser().getLogin(),
                            "new_user_password", addUserParamsDto.getUser().getPassword(),
                            "link", addUserParamsDto.getLink()
                    );
                } else {
                    yield Map.of();
                }
            }
            case BLOCK_SPECIALIST -> {
                if (notificationDto.getNotificationParameters()
                        instanceof BlockSpecialistParamsDto blockSpecialistParams) {
                    yield Map.of(
                            "methodologist_first_name", blockSpecialistParams.getMethodologist().getFirstName(),
                            "methodologist_last_name", blockSpecialistParams.getMethodologist().getLastName(),
                            "specialist_first_name", blockSpecialistParams.getSpecialist().getFirstName(),
                            "specialist_last_name", blockSpecialistParams.getSpecialist().getLastName()
                    );
                } else {
                    yield Map.of();
                }
            }
        };
    }

    public <T> String buildNotificationSubject(NotificationType type, T params) {
        return switch (type) {
            case REGISTER -> "Регистрация в сервисе “Будь лучше”.";
            case PASSWORD_RECOVERY -> "Восстановление пароля в сервисе “Будь лучше”.";
            case FEEDBACK -> {
                if (params instanceof FeedbackParamsDto feedbackParams) {
                    yield "Оставлен новый комментарий о специалисте %s %s от менеджера %s %s (%s)"
                            .formatted(
                                    feedbackParams.getSpecialist().getFirstName(),
                                    feedbackParams.getSpecialist().getLastName(),
                                    feedbackParams.getManagerInitiator().getFirstName(),
                                    feedbackParams.getManagerInitiator().getLastName(),
                                    "feedbackParams.getSpecialist().getProfession()"
                            );
                } else {
                    throw new IllegalArgumentException("Недопустимые параметры для типа обратной связи специалиста");
                }
            }
            case BLOCK_SPECIALIST -> {
                if (params instanceof BlockSpecialistParamsDto blockSpecialistParams) {
                    yield "Специалист %s %s ({profession}) заблокирован"
                            .formatted(
                                    blockSpecialistParams.getSpecialist().getFirstName(),
                                    blockSpecialistParams.getSpecialist().getLastName()
                            );
                } else {
                    throw new IllegalArgumentException("Недопустимые параметры для типа блокировки специалиста");
                }
            }
            case ADD_NEW_USER -> "Регистрация в сервисе “Будь лучше”";
        };
    }

}
