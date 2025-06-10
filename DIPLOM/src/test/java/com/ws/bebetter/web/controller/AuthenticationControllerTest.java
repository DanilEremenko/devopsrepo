package com.ws.bebetter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.config.SecurityConfig;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.web.dto.TokenRs;
import com.ws.bebetter.web.mapper.UserMapper;
import com.ws.java.junit.jupiter.params.provider.YamlFileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadConfig fileUploadConfig;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserMapper userMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @YamlFileSource(resource = "register-request-cases.yaml")
    void registerRequestDataShouldBeValidated(RegisterValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getRegistrationRq()));

        Mockito
                .doNothing()
                .when(authenticationService)
                .registerNewUser(Mockito.any());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "login-request-cases.yaml")
    void loginRequestDataShouldBeValidated(LoginValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/login/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getLoginRq()));

        Mockito
                .when(authenticationService.authenticateUser(Mockito.any()))
                .thenReturn(TokenRs.builder().build());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "logout-request-cases.yaml")
    @WithMockUser
    void logoutRequestDataShouldBeValidated(LogoutValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/logout/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getLogoutRq()));

        Mockito
                .doNothing()
                .when(authenticationService)
                .logoutUser(Mockito.any());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "password-recovery-request-cases.yaml")
    void passwordRecoveryRequestDataShouldBeValidated(PasswordRecoveryValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/password-recovery/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getPasswordRecoveryRq()));

        Mockito
                .doNothing()
                .when(authenticationService)
                .logoutUser(Mockito.any());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "password-reset-request-cases.yaml")
    void passwordResetRequestDataShouldBeValidated(PasswordResetValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/reset-password/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getPasswordResetRq()));

        Mockito
                .doNothing()
                .when(authenticationService)
                .resetPassword(Mockito.any());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "refresh-token-request-cases.yaml")
    @WithMockUser
    void refreshTokenRequestDataShouldBeValidated(RefreshTokenValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/refresh-token/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getRefreshTokenRq()));

        Mockito
                .when(authenticationService.refreshToken(Mockito.any()))
                .thenReturn(TokenRs.builder().build());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

    @ParameterizedTest
    @YamlFileSource(resource = "change-password-request-cases.yaml")
    @WithMockUser
    void changePasswordRequestDataShouldBeValidated(ChangePasswordValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/change-password/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getPasswordChangeRq()));

        Mockito
                .doNothing()
                .when(authenticationService)
                .changePassword(Mockito.any());

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s"
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }

}