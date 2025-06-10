package com.ws.bebetter.web.controller;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.FeedbackService;
import com.ws.bebetter.web.dto.FeedbackDto;
import com.ws.bebetter.web.dto.FeedbackSearchRequest;
import com.ws.bebetter.web.dto.ListPage;
import com.ws.bebetter.web.dto.ListResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback/")
@RequiredArgsConstructor
@Validated
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping(value = "{userId}/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createFeedback(@PathVariable("userId") User user, @Valid @RequestBody FeedbackDto feedbackDto) {
        feedbackService.createFeedback(user, feedbackDto);
    }

    @GetMapping(value = "{userId}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResult<FeedbackDto> getFeedbackList(@PathVariable("userId") User user,
                                                   @Valid @ModelAttribute FeedbackSearchRequest searchRequest) {
        Page<FeedbackDto> listPage = feedbackService.getFeedbackList(user, searchRequest);
        return ListResult.of(listPage.getContent(), new ListPage<>(listPage));
    }
}
