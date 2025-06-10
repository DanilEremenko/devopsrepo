package com.ws.bebetter.web.controller;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.service.AuthenticationService;
import com.ws.bebetter.web.dto.*;
import com.ws.bebetter.web.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("register/")
    public ResponseEntity<?> registerNewUserAndCompany(@RequestBody @Valid RegistrationRq registrationRq) {
        authenticationService.registerNewUser(registrationRq);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("login/")
    public TokenRs authenticateUser(@RequestBody @Valid LoginRq loginRq) {
        return authenticationService.authenticateUser(loginRq);
    }

    @PostMapping("logout/")
    public void logoutUser(@RequestBody @Valid LogoutRq logoutRq) {
        authenticationService.logoutUser(logoutRq);
    }

    @PostMapping("refresh-token/")
    public TokenRs refreshToken(@RequestBody @Valid RefreshTokenRq refreshTokenRq) {
        return authenticationService.refreshToken(refreshTokenRq);
    }

    @PostMapping("password-recovery/")
    public void passwordRecovery(@RequestBody @Valid PasswordRecoveryRq passwordRecoveryRq) {
        authenticationService.initPasswordRecovery(passwordRecoveryRq);
    }

    @PostMapping("reset-password/")
    public void resetPassword(@RequestBody @Valid PasswordResetRq passwordResetRq) {
        authenticationService.resetPassword(passwordResetRq);
    }

    @GetMapping("current-user/")
    public UserDto getAuthenticatedUser() {
        User user = authenticationService.getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @PostMapping("change-password/")
    public void changePassword(@RequestBody @Valid PasswordChangeRq passwordChangeRq) {
        authenticationService.changePassword(passwordChangeRq);
    }

}
