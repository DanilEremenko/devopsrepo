package com.ws.bebetter.service;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import com.ws.bebetter.exception.NotFoundException;

/**
 * Сервис для регистрации новых пользователей, аутентификации и авторизации.
 */
public interface AuthenticationService {

    /**
     * Регистрирует первого пользователя и новую компанию.
     *
     * @param registrationRq данные для регистрации нового пользователя и компании.
     */
    void registerNewUser(RegistrationRq registrationRq);

    /**
     * Аутентифицирует ранее зарегистрированного пользователя.
     *
     * @param loginRq объект, содержащий данные для аутентификации (логин и пароль пользователя).
     * @return {@link TokenRs} объект, содержащий данные для авторизации, включая accessToken и refreshToken.
     * @throws NotFoundException если пользователь не найден или переданы неверные учетные данные.
     */
    TokenRs authenticateUser(LoginRq loginRq);

    /**
     * Обновляет пару токенов (accessToken и refreshToken) для пользователя на основе переданного refreshTokenRq.
     *
     * @param refreshTokenRq объект, содержащий refreshToken, используемый для обновления токенов.
     * @return {@link TokenRs} объект, содержащий новую пару токенов (accessToken и refreshToken).
     * @throws AuthenticationServiceException если refreshToken недействителен или истек.
     */
    TokenRs refreshToken(RefreshTokenRq refreshTokenRq);

    /**
     * Выполняет выход пользователя из системы, access и refresh токены перестают быть действительными,
     * требуется повторный вход пользователя в систему.
     *
     * @param logoutRq объект, содержащий refresh токен для идентификации текущей сессии.
     */
    void logoutUser(LogoutRq logoutRq);

    /**
     * Генерирует ссылку для восстановления / установки нового пароля с добавлением идентификационного секрета.
     * Проверят наличие пользователя по указанному email.
     * Публикует событие для отправки почтового сообщения со ссылкой для установки пароля.
     *
     * @param passwordRecoveryRq объект, содержащий адрес пользователя для которого нужно
     *                           сгенерировать ссылку для восстановления пароля.
     * @throws com.ws.bebetter.exception.NotFoundException если пользователь с указанным email не найден.
     */
    void initPasswordRecovery(PasswordRecoveryRq passwordRecoveryRq);

    /**
     * Устанавливает новый пароль указанному пользователю.
     *
     * @param passwordResetRq учетные данные, содержащие в себе email, secret и password.
     * @throws com.ws.bebetter.exception.NotFoundException если пользователь с указанным email не найден.
     */
    void resetPassword(PasswordResetRq passwordResetRq);

    /**
     * Возвращает текущего авторизованного пользователя из контекста безопасности если такой существует.
     *
     * @return объект {@link UserDto}, объект, содержащий данные о текущем пользователе.
     */
    User getAuthenticatedUser();

    /**
     * Заменяет текущий пароль пользователя новым, если текущий пароль соответствует сохраненному в системе.
     */
    void changePassword(PasswordChangeRq passwordChangeRq);

}
