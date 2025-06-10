package com.ws.bebetter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@Data
@AllArgsConstructor
public class NotificationEvent<M> implements ResolvableTypeProvider {

    private User user;

    private NotificationType notificationType;

    private M notificationParameters;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(notificationParameters));
    }
}
