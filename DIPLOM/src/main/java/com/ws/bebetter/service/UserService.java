package com.ws.bebetter.service;

import com.ws.bebetter.entity.PasswordRecovery;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления пользователями.
 */
public interface UserService {

    /**
     * Создает нового пользователя на основе предоставленных данных.
     *
     * @param registrationRq DTO с данными для создания нового пользователя.
     * @return {@link UserDto} если пользователь успешно создан.
     */
    User createNewUser(RegistrationRq registrationRq);

    /**
     * Получает пользователя по указанному идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return {@link User} если пользователь существует.
     * @throws com.ws.bebetter.exception.NotFoundException если пользователь не найден.
     */
    User getUserById(UUID userId);

    /**
     * Получает пользователя по указанному логину.
     *
     * @param login логин пользователя.
     * @return {@link Optional<User>} объект, содержащий пользователя, если он найден,
     * или пустой, если пользователь с указанным логином не существует.
     */
    Optional<User> getUserByLogin(String login);

    /**
     * Устанавливает пользователю recovery-secret при восстановлении/установке пароля.
     *
     * @param user   пользователь, которому нужно установить recovery-secret.
     * @param secret recovery-secret
     */
    void setUserRecoverySecret(User user, String secret);

    /**
     * Получает действительные recovery-secret для восстановления/ установки пароля.
     *
     * @param user пользователь, recovery-secret которого нужно получить.
     * @return {@link List <String>} список recovery-secret.
     */
    List<PasswordRecovery> getUserRecoverySecrets(User user);

    /**
     * Получает действительные recovery-secret для восстановления/ установки пароля.
     *
     * @param user текущий аутентифицированный пользователь
     * @param addUserRq дто для доавления нового пользователя
     * @return {@link UserDto} Данные добавленного пользователя.
     */
    UserDto addNewUser(AddUserRq addUserRq, User user);

    /**
     * Возвращает страницу пользователей, соответствующих условиям фильтрации.
     *
     * @param request параметры фильтрации.
     * @return {@link Page} с элементами {@link User}.
     */
    Page<User> getUserList(UserListRequest request);

    /**
     * Обновление профиля пользователя методологом.
     *
     * @param editUserProfileRq данные для обновления профиля.
     * @param user              текущий
     * @return Подробная информация по профилю пользователя {@link UserProfileDetailsInfoRs}.
     */
    UserProfileDetailsInfoRs updateUserByMethodologist(User user, EditUserProfileRq editUserProfileRq);

    /**
     * Возвращает страницу пользователей, соответствующих условиям фильтрации.
     *
     * @param request параметры фильтрации.
     * @param currentUser данные текущего пользователя.
     * @return {@link Page} с элементами {@link UserListItemDto}.
     */
    Page<UserListItemDto> getUserListDto(UserListRequest request, User currentUser);

    /**
     * Применяет к указанному по идентификатору пользователю выбранное действие.
     *
     * @param userToApplyAction данные пользователя, для которого необходимо применить действие.
     * @param action действие, которое необходимо применить к пользователю
     * @param currentUser данные текущего пользователя
     */
    void applyUserAction(User userToApplyAction, UserActions action, User currentUser);

    /**
     * Получение детальной информации по профилю сотрудника специалистом компании.
     *
     * @param user пользователь, по которому запрашивается информация
     * @return Подробная информация по профилю пользователя {@link UserProfileDetailsInfoRs}.
     */
    UserProfileDetailsInfoRs getUserProfileInfo(User user);
}
