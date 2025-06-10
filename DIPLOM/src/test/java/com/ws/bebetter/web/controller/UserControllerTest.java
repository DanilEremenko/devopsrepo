package com.ws.bebetter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.config.SecurityConfig;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.UserService;
import com.ws.bebetter.web.dto.UserListItemDto;
import com.ws.java.junit.jupiter.params.provider.YamlFileSource;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FileUploadConfig fileUploadConfig;

    @MockBean
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @ParameterizedTest
    @YamlFileSource(resource = "edit-user-profile-by-methodologist-cases.yaml")
    @WithMockUser
    void sendDataShouldBeValidated(EditUserProfileByMethodologistValidationCase oneOfCase) throws Exception {
        // Arrange
        Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(new User());

        MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/users/update-user-profile/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getEditUserProfileRq()));

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

    @ParameterizedTest
    @YamlFileSource(resource = "user-list-cases.yaml")
    @WithMockUser
    void getUserListRequestShouldBeValidated(UserListValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = get("/api/v1/users/")
                .param("role", oneOfCase.getRequest().getRole().toString())
                .param("sortBy", oneOfCase.getRequest().getSortBy())
                .param("orderSort", oneOfCase.getRequest().getOrderSort())
                .param("pageNumber", String.valueOf(oneOfCase.getRequest().getPageNumber()))
                .param("countPerPage", String.valueOf(oneOfCase.getRequest().getCountPerPage()));

        PageImpl<UserListItemDto> page = new PageImpl<>(Collections.emptyList());
        Mockito
                .when(userService.getUserListDto(Mockito.any(), Mockito.any()))
                .thenReturn(page);

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assert
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
    @YamlFileSource(resource = "add-user-request-cases.yaml")
    @WithMockUser
    void sendAddNewUserRequestShouldBeValidated(AddNewUserValidationCase oneOfCase) throws Exception {
        // Arrange
        Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(new User());

        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneOfCase.getAddUserRq()));

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