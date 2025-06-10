package com.ws.bebetter.service;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.LoginRq;
import com.ws.bebetter.web.dto.LogoutRq;
import com.ws.bebetter.web.dto.RefreshTokenRq;
import com.ws.bebetter.web.dto.TokenRs;
import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Интерфейс для взаимодействия с Identity Provider для управления пользователями.
 */
public interface OAuthService {

    /**
     * Создает нового пользователя в системе.
     *
     * @param createdUser объект пользователя, содержащий данные для создания.
     */
    String createUser(User createdUser, String rawPassword);

    /**
     * Аутентифицирует существующего пользователя в системе.
     *
     * @param loginRq объект, содержащий данные для аутентификации (логин и пароль пользователя).
     * @return {@link TokenRs} объект, содержащий данные для авторизации, включая accessToken и refreshToken.
     */
    TokenRs authenticateUser(LoginRq loginRq);

    /**
     * Выполняет выход пользователя из системы, access и refresh токены перестают быть действительными,
     * требуется повторный вход пользователя в систему.
     *
     * @param logoutRq объект, содержащий refresh токен для идентификации текущей сессии.
     */
    void logoutUser(LogoutRq logoutRq);

    /**
     * Обновляет пару токенов (accessToken и refreshToken) для пользователя на основе переданного refreshTokenRq.
     *
     * @param refreshTokenRq объект, содержащий refreshToken, используемый для обновления токенов.
     * @return {@link TokenRs} объект, содержащий новую пару токенов (accessToken и refreshToken).
     * @throws AuthenticationServiceException если refreshToken недействителен или истек.
     */
    TokenRs refreshToken(RefreshTokenRq refreshTokenRq);

    /**
     * Устанавливает новый пароль пользователю.
     *
     * @param id          идентификатор пользователя.
     * @param newPassword новый пароль пользователя.
     */
    void setNewPasswordToUser(String id, String newPassword);

    /**
     * Обновляет пароль пользователю.
     *
     * @param id          идентификатор пользователя.
     * @param login       логин пользователя.
     * @param oldPassword действующий пароль пользователя.
     * @param newPassword новый пароль пользователя.
     */
    void updatePasswordToUser(String id, String login, String oldPassword, String newPassword);

    /**
     * Блокирует пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    void blockUser(String userId);

    /**
     * Разблокирует пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    void unblockUser(String userId);

}
