package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.*;
import com.ws.bebetter.exception.UserRegistrationException;
import com.ws.bebetter.properties.MailProperties;
import com.ws.bebetter.service.*;
import com.ws.bebetter.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final CompanyService companyService;
    private final OAuthService oAuthService;
    private final ApplicationEventPublisher eventPublisher;
    private final MailProperties mailProperties;

    @Override
    @Transactional
    public void registerNewUser(RegistrationRq registrationRq) {
        try {
            User newUser = userService.createNewUser(registrationRq);
            Company newCompany = companyService.createNewCompany(registrationRq.getCompany());
            userProfileService.createNewUserProfile(newUser, newCompany);
            String authorizeUrl = mailProperties.getDomain() + "auth/login";

            String keycloakId = oAuthService.createUser(newUser, registrationRq.getPassword());
            newUser.setKeycloakId(keycloakId);

            eventPublisher.publishEvent(new NotificationEvent<>(newUser, NotificationType.REGISTER, authorizeUrl));
        } catch (Exception e) {
            throw new UserRegistrationException("Ошибка при регистрации пользователя", e);
        }
    }

    @Override
    public TokenRs authenticateUser(LoginRq loginRq) {
        userService.getUserByLogin(loginRq.getLogin())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пользователь с логином %s не найден".formatted(loginRq.getLogin())));

        return oAuthService.authenticateUser(loginRq);
    }

    @Override
    public TokenRs refreshToken(RefreshTokenRq refreshTokenRq) {
        return oAuthService.refreshToken(refreshTokenRq);
    }

    @Override
    public void logoutUser(LogoutRq logoutRq) {
        oAuthService.logoutUser(logoutRq);
    }

    @Override
    @Transactional
    public void initPasswordRecovery(PasswordRecoveryRq passwordRecoveryRq) {
        String login = passwordRecoveryRq.getLogin();
        Optional<User> userOptional = userService.getUserByLogin(login);
        String secret = UUID.randomUUID().toString();

        userOptional.ifPresent(user -> {
            if (!userProfileService.getUserProfileByUser(user).getActiveStatus()) {
                throw new AccessDeniedException("Профиль пользователя должен быть активным");
            }
            userService.setUserRecoverySecret(user, secret);
            eventPublisher.publishEvent(new NotificationEvent<>(user, NotificationType.PASSWORD_RECOVERY, secret));
        });

        if (userOptional.isEmpty()) {
            eventPublisher.publishEvent(new NotificationEvent<>(null, NotificationType.PASSWORD_RECOVERY, secret));
        }

    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRq passwordResetRq) {
        User user = userService.getUserByLogin(passwordResetRq.getLogin())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пользователь с логином %s не найден"
                                .formatted(passwordResetRq.getLogin())));
        List<PasswordRecovery> userRecoverySecrets = userService.getUserRecoverySecrets(user);

        PasswordRecovery matchedSecret = userRecoverySecrets.stream()
                .filter(secret -> secret.getRecoverySecret().equals(passwordResetRq.getSecret()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Недействительный или просроченный " +
                        "секретный ключ для восстановления пароля"));

        matchedSecret.setUsed(true);

        oAuthService.setNewPasswordToUser(user.getKeycloakId(), passwordResetRq.getNewPassword());
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String login = jwt.getClaim("email");

            return userService.getUserByLogin(login)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Пользователь с логином %s не найден".formatted(login)));
        }

        return null;
    }

    @Override
    public void changePassword(PasswordChangeRq passwordChangeRq) {
        User user = getAuthenticatedUser();

        if (user == null) {
            return;
        }

        oAuthService.updatePasswordToUser(
                user.getKeycloakId(),
                user.getLogin(),
                passwordChangeRq.getOldPassword(),
                passwordChangeRq.getNewPassword()
        );
    }

}
