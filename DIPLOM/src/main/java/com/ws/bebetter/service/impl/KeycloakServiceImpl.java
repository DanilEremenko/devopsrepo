package com.ws.bebetter.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.exception.PasswordUpdateException;
import com.ws.bebetter.exception.UserBlockException;
import com.ws.bebetter.exception.UserRegistrationException;
import com.ws.bebetter.exception.UserUnblockException;
import com.ws.bebetter.properties.KeycloakConfig;
import com.ws.bebetter.service.OAuthService;
import com.ws.bebetter.web.dto.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements OAuthService {

    private final Keycloak keycloak;
    private final KeycloakConfig keycloakConfig;
    private final ObjectMapper objectMapper;

    @Override
    public String createUser(User createdUser, String rawPassword) {
        UserRepresentation userRepresentation = createNewUserRepresentation(createdUser, rawPassword);

        try {
            Response response = keycloak.realm(keycloakConfig.getApplicationRealm())
                    .users()
                    .create(userRepresentation);

            int status = response.getStatus();
            if (status != 201) {
                String errorMessage =
                        String.format("Ошибка создания пользователя %s в сервисе Keycloak. Статус: %d, причина: %s",
                        createdUser.getLogin(), status, response.readEntity(String.class));
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            String location = response.getHeaderString("Location");
            log.info("User {} successfully created in Keycloak", createdUser.getLogin());
            return location.substring(location.lastIndexOf("/") + 1);
        } catch (Exception e) {
            log.error("Failed to create user {} in Keycloak: {}", createdUser.getLogin(), e.getMessage());
            throw new UserRegistrationException("Ошибка создания пользователя в сервисе Keycloak", e);
        }
    }

    @Override
    public TokenRs authenticateUser(LoginRq loginRq) {
        String tokenUrl = keycloakConfig.getUrl() + "/realms/" + keycloakConfig.getApplicationRealm()
                + "/protocol/openid-connect/token";
        String body = String.format(
                "client_id=%s&client_secret=%s&grant_type=password&username=%s&password=%s",
                keycloakConfig.getClientId(),
                keycloakConfig.getClientSecret(),
                URLEncoder.encode(loginRq.getLogin(), StandardCharsets.UTF_8),
                URLEncoder.encode(loginRq.getPassword(), StandardCharsets.UTF_8)
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode != HttpStatus.OK.value()) {
                    log.error("Authentication failed for user {}: {}", loginRq.getLogin(), responseBody);
                    throw new AuthenticationServiceException("Неверное значение логина или пароля");
                }

                KeycloakTokenResponse keycloakTokenResponse = objectMapper.readValue(responseBody,
                        KeycloakTokenResponse.class);

                return TokenRs.builder()
                        .accessToken(keycloakTokenResponse.getAccessToken())
                        .refreshToken(keycloakTokenResponse.getRefreshToken())
                        .build();
            }

        } catch (Exception e) {
            log.error("Failed to authenticate user {}: {}", loginRq.getLogin(), e.getMessage());
            throw new AuthenticationServiceException("Ошибка при аутентификации", e);
        }
    }

    @Override
    public void logoutUser(LogoutRq logoutRq) {
        String tokenUrl = keycloakConfig.getUrl() + "/realms/" + keycloakConfig.getApplicationRealm()
                + "/protocol/openid-connect/logout";
        String body = String.format(
                "client_id=%s&client_secret=%s&refresh_token=%s",
                keycloakConfig.getClientId(),
                keycloakConfig.getClientSecret(),
                URLEncoder.encode(logoutRq.getRefreshToken(), StandardCharsets.UTF_8)
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.NO_CONTENT.value()) {
                    throw new AuthenticationServiceException("Выход из системы не удался");
                }

                log.info("Logout successful");
            }

        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            throw new AuthenticationServiceException("Ошибка при выходе из системы", e);
        }
    }

    @Override
    public TokenRs refreshToken(RefreshTokenRq refreshTokenRq) {
        String tokenUrl = keycloakConfig.getUrl() + "/realms/" + keycloakConfig.getApplicationRealm()
                + "/protocol/openid-connect/token";
        String body = String.format(
                "client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=%s",
                keycloakConfig.getClientId(),
                keycloakConfig.getClientSecret(),
                URLEncoder.encode(refreshTokenRq.getRefreshToken(), StandardCharsets.UTF_8)
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode != HttpStatus.OK.value()) {
                    log.error("Authentication failed");
                    throw new AuthenticationServiceException("Неверный логин или пароль");
                }

                KeycloakTokenResponse keycloakTokenResponse = objectMapper.readValue(responseBody,
                        KeycloakTokenResponse.class);

                return TokenRs.builder()
                        .accessToken(keycloakTokenResponse.getAccessToken())
                        .refreshToken(keycloakTokenResponse.getRefreshToken())
                        .build();
            }

        } catch (Exception e) {
            log.error("Failed to authenticate user");
            throw new AuthenticationServiceException("Ошибка при аутентификации", e);
        }
    }

    @Override
    public void setNewPasswordToUser(String id, String newPassword) {
        UserResource userResource = keycloak.realm(keycloakConfig.getApplicationRealm())
                .users()
                .get(id);

        CredentialRepresentation credentialRepresentation = createCredentialRepresentation(newPassword);

        try {
            userResource.resetPassword(credentialRepresentation);
            log.info("Password successfully updated for user with ID {}", id);
        } catch (Exception e) {
            log.error("Failed to update password for user with ID {}: {}", id, e.getMessage());
            throw new PasswordUpdateException("Ошибка обновления пароля в сервисе Keycloak", e);
        }
    }

    @Override
    public void updatePasswordToUser(String id, String login, String oldPassword, String newPassword) {
        authenticateUser(new LoginRq(login, oldPassword));

        setNewPasswordToUser(id, newPassword);
    }

    private UserRepresentation createNewUserRepresentation(User createdUser, String rawPassword) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(createdUser.getLogin());
        user.setUsername(createdUser.getLogin());
        user.setFirstName(createdUser.getFirstname());
        user.setLastName(createdUser.getLastname());
        user.setEnabled(true);

        CredentialRepresentation credentialRepresentation = createCredentialRepresentation(rawPassword);

        user.setCredentials(List.of(credentialRepresentation));
        return user;
    }

    private CredentialRepresentation createCredentialRepresentation(String rawPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(rawPassword);

        return credential;
    }

    @Override
    public void blockUser(String userId) {
        try {
            UserRepresentation user = keycloak.realm(keycloakConfig.getApplicationRealm())
                    .users()
                    .get(userId)
                    .toRepresentation();
            user.setEnabled(false);
            keycloak.realm(keycloakConfig.getApplicationRealm())
                    .users()
                    .get(userId)
                    .update(user);
            log.info("User with id {} is blocked in Keycloak", userId);
        } catch (Exception e) {
            log.error("Failed to block user with id {} in Keycloak: {}", userId, e.getMessage());
            throw new UserBlockException("Ошибка при блокировке пользователя", e);
        }
    }

    @Override
    public void unblockUser(String userId) {
        try {
            UserRepresentation user = keycloak.realm(keycloakConfig.getApplicationRealm())
                    .users()
                    .get(userId)
                    .toRepresentation();
            user.setEnabled(true);
            keycloak.realm(keycloakConfig.getApplicationRealm())
                    .users()
                    .get(userId)
                    .update(user);
            log.info("User with id {} is unblocked in Keycloak", userId);
        } catch (Exception e) {
            log.error("Failed to unblock user with id {} in Keycloak: {}", userId, e.getMessage());
            throw new UserUnblockException("Ошибка при разблокировке пользователя", e);
        }
    }
}

