package com.ws.bebetter.service;

import com.ws.bebetter.entity.Company;
import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.entity.UserProfile;
import com.ws.bebetter.web.dto.SetActiveRoleRq;
import com.ws.bebetter.web.dto.UpdateUserProfileRq;
import com.ws.bebetter.web.dto.UserProfileDetailsInfoRs;
import org.springframework.lang.NonNull;

/**
 * Сервис для управления профилями пользователей.
 */
public interface UserProfileService {

    /**
     * Создает новый профиль пользователя на основе предоставленных данных.
     *
     * @param user    Сущность пользователя.
     * @param company Новая компания.
     */
    void createNewUserProfile(User user, Company company);

    /**
     * Возвращает профиль пользователя по идентификатору пользователя (родительская, связанная сущность).
     *
     * @param user Объект пользователя.
     * @return {@link UserProfile} Профиль пользователя.
     * @throws com.ws.bebetter.exception.NotFoundException если профиль пользователя не найден.
     */
    @NonNull
    UserProfile getUserProfileByUser(User user);

    /**
     * Устанавливает активную роль для пользователя.
     *
     * @param user            текущий пользователь
     * @param setActiveRoleRq данные для установки активной роли.
     */
    void setActiveRole(User user, SetActiveRoleRq setActiveRoleRq);

    /**
     * Получение информации о текущем профиле пользователя с перечнем ролей и указанием активной роли
     *
     * @param user данные текущего пользователя.
     * @return {@link com.ws.bebetter.web.dto.UserProfileDetailsInfoRs}
     */
    UserProfileDetailsInfoRs getUserProfileDetailsInfo(User user);

    /**
     * Обновление профиля пользователем
     *
     * @param user                текущий пользователь
     * @param updateUserProfileRq данные для обновления профиля
     * @return {@link com.ws.bebetter.web.dto.UserProfileDetailsInfoRs}
     */
    UserProfileDetailsInfoRs updateUserProfileByCurrentUser(User user, UpdateUserProfileRq updateUserProfileRq);

    /**
     * Проверка активной роли пользователя для доступа к выполнению операции
     *
     * @param user     пользователь
     * @param roleType тип активной роли
     * @return {@link com.ws.bebetter.entity.UserProfile} профиль пользователя прошедшего валидацию по активной роли
     */
    @NonNull
    UserProfile checkActiveUserProfileRole(User user, RoleType... roleType);
}