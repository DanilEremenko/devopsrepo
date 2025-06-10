package com.ws.bebetter.service;

import com.ws.bebetter.entity.NotificationDto;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.InternalNotificationsListItemRs;
import com.ws.bebetter.web.dto.InternalNotificationsListRq;
import org.springframework.data.domain.Page;

/**
 * Сервис для обработки и получения внутренних уведомлений.
 */
public interface InternalNotificationsService {

    /**
     * Слушает очередь событий и накапливает уведомления в базе данных для последующей выдачи.
     *
     * @param notificationDto параметризованный объект, содержащий данные об уведомлении.
     */
    <T> void accumulateInternalNotifications(NotificationDto<T> notificationDto);

    /**
     * Получает страницу, содержащую уведомления пользователя.
     *
     * @param internalNotificationsListRq данные пагинации страницы уведомлений
     *                                    (номер страницы, количество элементов на странице)
     * @param user                        пользователь, для которого запрашивается список уведомлений.
     * @return {@link InternalNotificationsListItemRs} страница, содержащая уведомления пользователя
     *                                                 по запрашиваемому формату пагинации.
     */
    Page<InternalNotificationsListItemRs> getUserNotificationsList(
            InternalNotificationsListRq internalNotificationsListRq, User user);

    /**
     * Возвращает количество непрочитанных уведомлений у текущего пользователя.
     *
     * @param currentUser учетные данные текущего пользователя для фильтрации запроса.
     * @return {@link Long} количество непрочитанных уведомлений пользователя.
     */
    Long getCountOfUnreadNotifications(User currentUser);

}
