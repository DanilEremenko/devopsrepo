package com.ws.bebetter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.config.SecurityConfig;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.FeedbackService;
import com.ws.bebetter.web.dto.FeedbackDto;
import com.ws.java.junit.jupiter.params.provider.YamlFileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@Import(SecurityConfig.class)
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadConfig fileUploadConfig;

    @MockBean
    private FeedbackService feedbackService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Converter<String, User> userConverter() {
            return new Converter<>() {
                @Override
                public User convert(String source) {
                    return new User();
                }
            };
        }
    }

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @ParameterizedTest
    @YamlFileSource(resource = "feedback-creation-cases.yaml")
    @WithMockUser
    void createDataShouldBeValidated(FeedbackValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/feedback/{userId}/", oneOfCase.getUserId())
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

    @ParameterizedTest
    @YamlFileSource(resource = "feedback-list-cases.yaml")
    @WithMockUser
    void getUserListRequestShouldBeValidated(FeedbackListValidationCase oneOfCase) throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = get("/api/v1/feedback/{userId}/", oneOfCase.getUserId())
                .param("page", String.valueOf(oneOfCase.getRequest().getPage()))
                .param("count", String.valueOf(oneOfCase.getRequest().getCount()));

        PageImpl<FeedbackDto> page = new PageImpl<>(Collections.emptyList());
        Mockito
                .when(feedbackService.getFeedbackList(Mockito.any(), Mockito.any()))
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

}
