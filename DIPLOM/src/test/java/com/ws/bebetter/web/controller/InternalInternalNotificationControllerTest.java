package com.ws.bebetter.web.controller;

import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.config.SecurityConfig;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.InternalNotificationsService;
import com.ws.java.junit.jupiter.params.provider.YamlFileSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalNotificationController.class)
@Import(SecurityConfig.class)
class InternalInternalNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InternalNotificationsService internalNotificationsService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @MockBean
    private InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;

    @MockBean
    private FileUploadConfig fileUploadConfig;

    @ParameterizedTest
    @YamlFileSource(resource = "notifications-list-request-cases.yaml")
    @WithMockUser
    void notificationsListRequestDataShouldBeValidated(NotificationsListValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = get("/api/v1/notifications/");

        if (oneOfCase.getInternalNotificationsListRq().getPageNumber() != null) {
            requestBuilder.param("pageNumber", oneOfCase.getInternalNotificationsListRq().getPageNumber().toString());
        }

        if (oneOfCase.getInternalNotificationsListRq().getCountPerPage() != null) {
            requestBuilder.param("countPerPage",
                    oneOfCase.getInternalNotificationsListRq().getCountPerPage().toString());
        }

        Mockito
                .when(internalNotificationsService.getUserNotificationsList(Mockito.any(), Mockito.any()))
                .thenReturn(Page.empty());

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