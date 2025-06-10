package com.ws.bebetter.web.controller;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.InternalNotificationsService;
import com.ws.bebetter.web.dto.InternalNotificationsListItemRs;
import com.ws.bebetter.web.dto.InternalNotificationsListRq;
import com.ws.bebetter.web.dto.ListPage;
import com.ws.bebetter.web.dto.ListResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications/")
public class InternalNotificationController {

    private final InternalNotificationsService internalNotificationsService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ListResult<InternalNotificationsListItemRs> getUserNotificationsList(
            @Valid @ModelAttribute InternalNotificationsListRq internalNotificationsListRq) {
        User currentUser = authenticationService.getAuthenticatedUser();
        Page<InternalNotificationsListItemRs> listPage = internalNotificationsService
                .getUserNotificationsList(internalNotificationsListRq, currentUser);

        return ListResult.of(listPage.getContent(), new ListPage<>(listPage));
    }

    @GetMapping("unread/")
    public Long getCountOfUnreadNotifications() {
        User currentUser = authenticationService.getAuthenticatedUser();
        return internalNotificationsService.getCountOfUnreadNotifications(currentUser);
    }

}

