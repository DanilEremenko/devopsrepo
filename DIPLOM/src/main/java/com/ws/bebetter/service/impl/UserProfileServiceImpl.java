package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.*;
import com.ws.bebetter.exception.ActiveRoleNotSpecifiedException;
import com.ws.bebetter.exception.NotFoundException;
import com.ws.bebetter.exception.OperationNotAllowedByCurrentUserRole;
import com.ws.bebetter.exception.RoleNotAllowedException;
import com.ws.bebetter.repository.UserProfileRepository;
import com.ws.bebetter.service.FileRefService;
import com.ws.bebetter.service.UserProfileService;
import com.ws.bebetter.util.TimeUtil;
import com.ws.bebetter.web.dto.*;
import com.ws.bebetter.web.mapper.FileRefMapper;
import com.ws.bebetter.web.mapper.UserRoleMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRoleMapper userRoleMapper;
    private final TimeUtil timeUtil;
    private final FileRefService fileRefService;
    private final FileRefMapper fileRefMapper;

    @Override
    public void createNewUserProfile(User user, Company company) {
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .company(company)
                .registrationDate(timeUtil.getCurrentDate())
                .updateDate(timeUtil.getCurrentDate())
                .activeStatus(true)
                .build();

        UserProfile newUserProfile = userProfileRepository.save(userProfile);
        log.info("UserProfile created with ID: {}", newUserProfile.getId());
    }

    @Override
    @NonNull
    public UserProfile getUserProfileByUser(User user) {
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Профиль не найден для пользователя с идентификатором %s"
                        .formatted(user.getId())));
    }

    @Override
    @Transactional
    public void setActiveRole(User user, SetActiveRoleRq setActiveRoleRq) {
        RoleType activeRole = setActiveRoleRq.getActiveRole();

        if (rightAccessCheck(user, activeRole)) {
            UserProfile userProfile = userProfileRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Профиль не найден для пользователя с логином %s"
                            .formatted(user.getLogin())));

            Set<UserRole> allowedUserRoles = user.getUserRoles();
            UserRole activeUserRole = allowedUserRoles.stream()
                    .filter(userRole -> userRole.getRoleType().equals(activeRole)).findFirst()
                    .orElseThrow(() ->
                            new NotFoundException("Активная роль %s отсутствует в списке разрешенных ролей пользователя"
                                    .formatted(activeRole)));
            userProfile.setActiveRole(activeUserRole);
            log.info("Active role {} set for user {}", activeRole, user.getLogin());

        } else {
            throw new RoleNotAllowedException("Невозможно установить роль %s для указанного пользователя"
                    .formatted(setActiveRoleRq.getActiveRole()));
        }
    }

    @Override
    public UserProfileDetailsInfoRs getUserProfileDetailsInfo(User user) {
        UserProfile profileCurrentUser = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Профиль не найден для пользователя с логином %s"
                        .formatted(user.getLogin())));

        if (profileCurrentUser.getActiveRole() == null) {
            throw new IllegalStateException("Установите активную роль в вашем профиле " +
                    "для дальнейшего взаимодействия с системой");
        }

        Set<UserRoleDto> userRoleRsSet = user.getUserRoles().stream()
                .map(userRoleMapper::buildUserRoleDto)
                .collect(Collectors.toSet());

        return UserProfileDetailsInfoRs.builder()
                .id(profileCurrentUser.getId())
                .login(user.getLogin())
                .photo(fileRefMapper.toDto(profileCurrentUser.getPhoto()))
                .activeRole(userRoleMapper.buildUserRoleDto(profileCurrentUser.getActiveRole()))
                .roles(userRoleRsSet)
                .dateOfBirth(profileCurrentUser.getDateOfBirth())
                .lastName(user.getLastname())
                .firstName(user.getFirstname())
                .middleName(user.getMiddlename())
                .workExperience(profileCurrentUser.getWorkExperience())
                .activeStatus(profileCurrentUser.getActiveStatus())
                .company(CompanyDto.builder()
                        .inn(profileCurrentUser.getCompany().getInn())
                        .companyTitle(profileCurrentUser.getCompany().getName())
                        .build())
                .build();
    }

    private boolean rightAccessCheck(User user, RoleType roleType) {
        return user.getUserRoles().stream()
                .anyMatch(role -> role.getRoleType().equals(roleType));
    }

    @Override
    @Transactional
    public UserProfileDetailsInfoRs updateUserProfileByCurrentUser(User user, UpdateUserProfileRq updateUserProfileRq) {
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Профиль не найден для пользователя с логином %s"
                        .formatted(user.getLogin())));

        if (userProfile.getActiveRole() == null) {
            throw new ActiveRoleNotSpecifiedException("Установите активную роль в вашем профиле " +
                    "для дальнейшего взаимодействия с системой");
        }

        fileRefService.setPhotoToProfile(updateUserProfileRq.getPhoto(), userProfile);

        userProfile.setDateOfBirth(updateUserProfileRq.getDateOfBirth() != null ? updateUserProfileRq.getDateOfBirth() :
                userProfile.getDateOfBirth());

        userProfile.setWorkExperience(updateUserProfileRq.getWorkExperience() != null ?
                updateUserProfileRq.getWorkExperience() : userProfile.getWorkExperience());

        userProfile.getUser().setMiddlename(updateUserProfileRq.getMiddleName() != null ? updateUserProfileRq.getMiddleName() :
                userProfile.getUser().getMiddlename());

        Set<UserRoleDto> userRoleSet = user.getUserRoles().stream().map(userRoleMapper::buildUserRoleDto)
                .collect(Collectors.toSet());

        return UserProfileDetailsInfoRs.builder()
                .id(userProfile.getId())
                .lastName(user.getLastname())
                .firstName(user.getFirstname())
                .middleName(user.getMiddlename())
                .roles(userRoleSet)
                .activeRole(userRoleMapper.buildUserRoleDto(userProfile.getActiveRole()))
                .activeStatus(userProfile.getActiveStatus())
                .photo(fileRefMapper.toDto(userProfile.getPhoto()))
                .dateOfBirth(userProfile.getDateOfBirth())
                .workExperience(userProfile.getWorkExperience())
                .login(user.getLogin())
                .company(CompanyDto.builder()
                        .inn(userProfile.getCompany().getInn())
                        .companyTitle(userProfile.getCompany().getName())
                        .build())
                .build();
    }

    @NonNull
    @Override
    public UserProfile checkActiveUserProfileRole(User user, RoleType... roleTypes) {
        var profile = getUserProfileByUser(user);

        boolean hasValidRole = Arrays.stream(roleTypes)
                .anyMatch(roleType -> profile.getActiveRole() != null &&
                        profile.getActiveRole().getRoleType().equals(roleType));

        if (!hasValidRole) {
            throw new OperationNotAllowedByCurrentUserRole(
                    "Операция разрешена только для пользователей с одной из активных ролей: " +
                            Arrays.toString(roleTypes));
        }

        return profile;
    }
}