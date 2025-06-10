package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.*;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.FeedbackService;
import com.ws.bebetter.service.UserProfileService;
import com.ws.bebetter.service.UserService;
import com.ws.bebetter.util.TimeUtil;
import com.ws.bebetter.web.dto.*;
import com.ws.bebetter.web.mapper.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationService authenticationService;
    private final TimeUtil timeUtil;
    private final FeedbackMapper feedbackMapper;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public void createFeedback(User user, FeedbackDto feedbackDto) {
        User currentUser = authenticationService.getAuthenticatedUser();
        userProfileService.checkActiveUserProfileRole(currentUser, RoleType.MANAGER);

        userProfileService.checkActiveUserProfileRole(user, RoleType.SPECIALIST);

        Feedback feedback = Feedback.builder()
                .author(currentUser)
                .message(feedbackDto.getMessage())
                .createdAt(timeUtil.getCurrentDate())
                .build();
        List<Feedback> feedbacks = user.getFeedback();
        feedbacks.add(feedback);
        user.setFeedback(feedbacks);

        log.info("Feedback {} assign to specialist {}", feedback, user);

        List<User> allManagers = userService.getUserList(UserListRequest.builder()
                        .role(RoleType.MANAGER)
                        .build())
                .stream()
                .filter(manager -> !manager.equals(currentUser))
                .toList();

        allManagers.forEach(value -> eventPublisher.publishEvent(new NotificationEvent<>(value,
                NotificationType.FEEDBACK,
                FeedbackParamsDto.builder()
                        .managerInitiator(UserShortDto.builder()
                                .id(currentUser.getId())
                                .firstName(currentUser.getFirstname())
                                .lastName(currentUser.getLastname())
                                .build())
                        .managerRecipient(UserShortDto.builder()
                                .id(value.getId())
                                .firstName(value.getFirstname())
                                .lastName(value.getLastname())
                                .build())
                        .specialist(UserShortDto.builder()
                                .id(user.getId())
                                .firstName(user.getFirstname())
                                .lastName(user.getLastname())
                                .build())
                        .build())));

    }

    @Override
    public Page<FeedbackDto> getFeedbackList(User user, FeedbackSearchRequest searchRequest) {
        User currentUser = authenticationService.getAuthenticatedUser();
        userProfileService.checkActiveUserProfileRole(currentUser, RoleType.MANAGER);

        List<FeedbackDto> list = user.getFeedback().stream()
                .sorted(Comparator.comparing(Feedback::getCreatedAt).reversed())
                .map(feedbackMapper::toDto)
                .toList();

        Pageable pageable = PageRequest.of(searchRequest.getPage() - 1, searchRequest.getCount());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
