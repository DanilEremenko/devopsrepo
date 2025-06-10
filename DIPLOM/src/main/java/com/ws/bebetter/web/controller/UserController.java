package com.ws.bebetter.web.controller;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.service.UserService;
import com.ws.bebetter.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("{id}/")
    public UserProfileDetailsInfoRs getUserProfileInfo(@PathVariable("id") User user) {
        return userService.getUserProfileInfo(user);
    }

    @PatchMapping("update-user-profile/")
    public UserProfileDetailsInfoRs updateUserProfile(@Valid @RequestBody EditUserProfileRq editUserProfileRq) {
        return userService
                .updateUserByMethodologist(Objects.requireNonNull(authenticationService.getAuthenticatedUser()),
                        editUserProfileRq);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResult<UserListItemDto> getUserList(@Valid @ModelAttribute UserListRequest request) {
        User currentUser = authenticationService.getAuthenticatedUser();

        Page<UserListItemDto> listPage = userService.getUserListDto(request, currentUser);
        return ListResult.of(listPage.getContent(), new ListPage<>(listPage));
    }

    @PostMapping("{id}/{action}/")
    public void applyUserAction(@PathVariable("id") User user, @PathVariable UserActions action) {
        User currentUser = authenticationService.getAuthenticatedUser();

        userService.applyUserAction(user, action, currentUser);
    }

    @PostMapping
    public UserDto addNewUser(@RequestBody @Valid AddUserRq addUserRq) {
        return userService.addNewUser(addUserRq,
                Objects.requireNonNull(authenticationService.getAuthenticatedUser()));
    }
}
