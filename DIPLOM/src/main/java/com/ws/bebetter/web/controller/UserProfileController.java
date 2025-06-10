package com.ws.bebetter.web.controller;

import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.UserProfileService;
import com.ws.bebetter.web.dto.SetActiveRoleRq;
import com.ws.bebetter.web.dto.UpdateUserProfileRq;
import com.ws.bebetter.web.dto.UserProfileDetailsInfoRs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/v1/user-profiles/")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final AuthenticationService authenticationService;

    @PostMapping("set-active-role/")
    public void setActiveRole(@RequestBody @Valid SetActiveRoleRq setActiveRoleRq) {
        var currentUser = authenticationService.getAuthenticatedUser();
        userProfileService.setActiveRole(Objects.requireNonNull(currentUser),
                setActiveRoleRq);
    }

    @PostMapping("update-user-profile/")
    public UserProfileDetailsInfoRs updateUserProfileByCurrentUser(
            @RequestBody @Valid UpdateUserProfileRq updateUserProfileRq) {

        return userProfileService.updateUserProfileByCurrentUser(
                Objects.requireNonNull(authenticationService.getAuthenticatedUser()),
                updateUserProfileRq);
    }

    @GetMapping
    public UserProfileDetailsInfoRs getUserProfile() {
        return userProfileService.getUserProfileDetailsInfo(Objects.requireNonNull(
                authenticationService.getAuthenticatedUser()
        ));
    }
}
