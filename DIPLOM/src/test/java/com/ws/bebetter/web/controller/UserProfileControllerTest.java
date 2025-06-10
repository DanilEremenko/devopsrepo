package com.ws.bebetter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.config.SecurityConfig;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.UserProfileService;
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

@WebMvcTest(UserProfileController.class)
@Import(SecurityConfig.class)
public class UserProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FileUploadConfig fileUploadConfig;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @ParameterizedTest
    @YamlFileSource(resource = "user-profile-set-active-role-cases.yaml")
    @WithMockUser
    void sendDataShouldBeValidated(UserProfileValidationCase oneOfCase) throws Exception {
        // Arrange
        Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(new User());

        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/user-profiles/set-active-role/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getData()));

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assertion
        ResultMatcher resultStatusMatcher = oneOfCase.isValid() ? status().is2xxSuccessful() : status().isBadRequest();

        try {
            resultActions
                    .andExpect(resultStatusMatcher)
            ;
        } catch (AssertionError e) {
            throw new AssertionError("Comment: %s, expectation error: %s."
                    .formatted(oneOfCase.getComment(), e.getMessage()));
        }
    }
}