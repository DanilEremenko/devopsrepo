package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.*;
import com.ws.bebetter.exception.NotFoundException;
import com.ws.bebetter.exception.ResourceAlreadyExistsException;
import com.ws.bebetter.properties.MailProperties;
import com.ws.bebetter.repository.PasswordRecoveryRepository;
import com.ws.bebetter.repository.UserRepository;
import com.ws.bebetter.repository.UserRoleRepository;
import com.ws.bebetter.service.FileRefService;
import com.ws.bebetter.service.OAuthService;
import com.ws.bebetter.service.UserProfileService;
import com.ws.bebetter.service.UserService;
import com.ws.bebetter.util.PasswordGeneratorUtil;
import com.ws.bebetter.util.TimeUtil;
import com.ws.bebetter.web.dto.*;
import com.ws.bebetter.web.mapper.CompanyMapper;
import com.ws.bebetter.web.mapper.FileRefMapper;
import com.ws.bebetter.web.mapper.UserRoleMapper;
import jakarta.persistence.criteria.*;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TimeUtil timeUtil;
    private final UserRepository userRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final FileRefService fileRefService;
    private final UserRoleRepository userRoleRepository;
    private final UserProfileService userProfileService;
    private final OAuthService oAuthService;
    private final UserRoleMapper userRoleMapper;
    private final CompanyMapper companyMapper;
    private final PasswordGeneratorUtil passwordGeneratorUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final MailProperties mailProperties;
    private final FileRefMapper fileRefMapper;

    @Override
    public User createNewUser(RegistrationRq registrationRq) {

        if (userRepository.findByLogin(registrationRq.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Пользователь с email %s уже существует"
                    .formatted(registrationRq.getEmail()));
        }

        User user = User.builder()
                .lastname(registrationRq.getLastName())
                .firstname(registrationRq.getFirstName())
                .middlename(registrationRq.getMiddleName() != null ?
                        registrationRq.getMiddleName() : null)
                .login(registrationRq.getEmail())
                .build();

        UserRole methodologistRole = UserRole.builder()
                .user(user)
                .roleType(RoleType.METHODOLOGIST)
                .build();

        UserRole mentorRole = UserRole.builder()
                .user(user)
                .roleType(RoleType.MENTOR)
                .build();

        user.setUserRoles(Set.of(methodologistRole, mentorRole));

        userRepository.save(user);
        log.info("User created with ID: {}", user.getId());
        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с идентификатором %s не найден"
                        .formatted(userId)));

    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public void setUserRecoverySecret(User user, String secret) {
        PasswordRecovery recovery = PasswordRecovery.builder()
                .user(user)
                .recoverySecret(secret)
                .used(false)
                .createdAt(timeUtil.getCurrentDate())
                .build();

        passwordRecoveryRepository.save(recovery);
    }

    @Override
    public List<PasswordRecovery> getUserRecoverySecrets(User user) {
        return passwordRecoveryRepository.findAllByUserId(user.getId()).stream()
                .filter(secret -> !secret.getUsed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addNewUser(AddUserRq addUserRq, User user) {
        UserProfile methodologistProfile = userProfileService.checkActiveUserProfileRole(user, RoleType.METHODOLOGIST);

        String password = passwordGeneratorUtil.generateRandomValidPassword();

        User addedUser = buildAddedUser(addUserRq);

        Set<UserRole> addedUserRoles = createRoleSetForAddedUser(addUserRq.getRoles(), addedUser);

        UserRole activeUserRole = configureActiveUserRole(addedUserRoles);

        addedUser.setUserRoles(addedUserRoles);

        String keyCloakId = oAuthService.createUser(addedUser, password);
        addedUser.setKeycloakId(keyCloakId);

        userRepository.save(addedUser);

        userProfileService.createNewUserProfile(addedUser, methodologistProfile.getCompany());
        userProfileService.getUserProfileByUser(addedUser).setActiveRole(activeUserRole);

        String addNewUserUrl = mailProperties.getDomain() + "auth/login/";

        eventPublisher.publishEvent(new NotificationEvent<>(addedUser, NotificationType.ADD_NEW_USER,
                AddUserParamsDto.builder()
                        .user(LoginRq.builder()
                                .login(addedUser.getLogin())
                                .password(password)
                                .build())
                        .methodologist(UserShortDto.builder()
                                .firstName(user.getFirstname())
                                .lastName(user.getLastname())
                                .build())
                        .link(addNewUserUrl)
                        .build()));

        return UserDto.builder()
                .id(addedUser.getId())
                .firstname(addUserRq.getFirstName())
                .lastname(addUserRq.getLastName())
                .middlename(addUserRq.getMiddleName())
                .login(addUserRq.getLogin())
                .userRoles(addedUser.getUserRoles().stream().map(userRoleMapper::buildUserRoleDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public Page<User> getUserList(UserListRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPageNumber() - 1,
                searchRequest.getCountPerPage());

        return userRepository.findAll(
                (root, query, cb) -> {
                    assert query != null;

                    Predicate criteria = cb.conjunction();
                    if (searchRequest.getLastname() != null) {
                        Predicate predicate = cb.like((root.get(User_.lastname)),
                                "%" + searchRequest.getLastname() + "%");
                        criteria = cb.and(criteria, predicate);
                    }
                    if (searchRequest.getRole() != null) {
                        Join<User, UserRole> userRoleJoin = root.join(User_.userRoles);
                        Predicate predicate = cb.equal(userRoleJoin.get(UserRole_.roleType),
                                searchRequest.getRole());
                        criteria = cb.and(criteria, predicate);
                    }

                    Function<Expression<?>, Order> sortFn = cb::asc;
                    switch (searchRequest.getSortBy()) {
                        case "lastname" -> query.orderBy(sortFn.apply(root.get(User_.lastname)));
                        case "registrationDate" -> {
                            Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
                            Root<UserProfile> userProfileRoot = subquery.from(UserProfile.class);
                            subquery.select(userProfileRoot.get(UserProfile_.registrationDate))
                                    .where(cb.equal(userProfileRoot.get(UserProfile_.user), root));
                            query.orderBy(sortFn.apply(subquery.getSelection()));
                        }
                        case "updateDate" -> {
                            Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
                            Root<UserProfile> userProfileRoot = subquery.from(UserProfile.class);
                            subquery.select(userProfileRoot.get(UserProfile_.updateDate))
                                    .where(cb.equal(userProfileRoot.get(UserProfile_.user), root));
                            query.orderBy(sortFn.apply(subquery.getSelection()));
                        }
                        case "activeStatus" -> {
                            Subquery<Boolean> subquery = query.subquery(Boolean.class);
                            Root<UserProfile> userProfileRoot = subquery.from(UserProfile.class);
                            subquery.select(userProfileRoot.get(UserProfile_.activeStatus))
                                    .where(cb.equal(userProfileRoot.get(UserProfile_.user), root));
                            query.orderBy(sortFn.apply(subquery.getSelection()));
                        }
                    }
                    return criteria;
                },
                pageable
        );
    }

    @Override
    public Page<UserListItemDto> getUserListDto(UserListRequest request, User currentUser) {
        userProfileService.checkActiveUserProfileRole(currentUser, RoleType.METHODOLOGIST, RoleType.MENTOR,
                RoleType.MANAGER);

        return getUserList(request)
                .map(user -> UserListItemDto.builder()
                        .id(user.getId())
                        .fullname("%s %s".formatted(user.getLastname(), user.getFirstname()))
                        .role(user.getUserRoles().stream()
                                .map(role -> role.getRoleType().toString()).collect(Collectors.toSet()))
                        .activeStatus(userProfileService.getUserProfileByUser(user).getActiveStatus())
                        .registrationDate(userProfileService.getUserProfileByUser(user).getRegistrationDate())
                        .updateDate(userProfileService.getUserProfileByUser(user).getUpdateDate())
                        .build());
    }

    @Override
    @Transactional
    public UserProfileDetailsInfoRs updateUserByMethodologist(User user, EditUserProfileRq editUserProfileRq) {
        userProfileService.checkActiveUserProfileRole(user, RoleType.METHODOLOGIST);

        var editedUser = userRepository.findById(editUserProfileRq.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором %s не найден"
                        .formatted(editUserProfileRq.getUserId())));

        var editedUserProfile = userProfileService.getUserProfileByUser(editedUser);

        editedUser.setLastname(editUserProfileRq.getLastName());
        editedUser.setFirstname(editUserProfileRq.getFirstName());
        editedUser.setMiddlename(editUserProfileRq.getMiddleName() != null ?
                editUserProfileRq.getMiddleName() : editedUser.getMiddlename());
        editedUser.setLogin(editUserProfileRq.getLogin());

        if (editUserProfileRq.getRoles() != null && !editUserProfileRq.getRoles().isEmpty()) {
            Set<UserRole> updateUserRoleSet = editUserProfileRq.getRoles().stream()
                    .map(roleType -> userRoleRepository.findByUserAndRoleType(editedUser, roleType)
                            .orElse(UserRole.builder().user(editedUser).roleType(roleType).build()))
                    .collect(Collectors.toSet());

            editedUser.getUserRoles().clear();

            editedUser.setUserRoles(updateUserRoleSet);
        }

        fileRefService.setPhotoToProfile(editUserProfileRq.getPhoto(), editedUserProfile);

        editedUserProfile.setDateOfBirth(editUserProfileRq.getDateOfBirth() != null ?
                editUserProfileRq.getDateOfBirth() : editedUserProfile.getDateOfBirth());
        editedUserProfile.setWorkExperience(editUserProfileRq.getWorkExperience() != null ?
                editUserProfileRq.getWorkExperience() : editedUserProfile.getWorkExperience());

        editedUserProfile.setActiveRole(configureActiveUserRole(editedUser.getUserRoles()));

        editedUserProfile.setActiveStatus(editUserProfileRq.getActiveStatus() != null ?
                editUserProfileRq.getActiveStatus() : editedUserProfile.getActiveStatus());

        log.info("METHODOLOGIST {} updated user {}", user.getLogin(), editedUser.getLogin());

        return buildUserProfileDetailsInfoRs(editedUser, editedUserProfile);
    }

    @Override
    @Transactional
    public void applyUserAction(User userToApplyAction, UserActions action, User currentUser) {
        userProfileService.checkActiveUserProfileRole(currentUser, RoleType.METHODOLOGIST);
        switch (action) {
            case UNBLOCK -> unblockUser(userToApplyAction);
            case BLOCK -> blockUser(userToApplyAction, currentUser);
        }

        log.info("Action '{}' has been applied to the {}", action, userToApplyAction);
    }

    private void unblockUser(User user) {
        UserProfile userProfile = userProfileService.getUserProfileByUser(user);

        if (!userProfile.getActiveStatus()) {
            userProfile.setActiveStatus(true);
            oAuthService.unblockUser(user.getKeycloakId());
        }
    }

    private void blockUser(User userToApplyAction, User currentUser) {
        UserProfile userProfile = userProfileService.getUserProfileByUser(userToApplyAction);
        userToApplyAction.getUserRoles().forEach(role -> {
            switch (role.getRoleType()) {
                case SPECIALIST -> blockRoleSpecialist(currentUser, userToApplyAction);
                case METHODOLOGIST -> blockRoleMethodologist(userToApplyAction);
            }
        });

        if (userProfile.getActiveStatus()) {
            userProfile.setActiveStatus(false);
            oAuthService.blockUser(userToApplyAction.getKeycloakId());
        }
    }

    private void blockRoleSpecialist(User currentUser, User userToApplyAction) {
        List<User> allManagers = getUserList(UserListRequest.builder()
                .role(RoleType.MANAGER)
                .build())
                .toList();
        allManagers.forEach(value ->
                eventPublisher.publishEvent(new NotificationEvent<>(value, NotificationType.BLOCK_SPECIALIST,
                        BlockSpecialistParamsDto.builder()
                                .methodologist(UserShortDto.builder()
                                        .id(currentUser.getId())
                                        .firstName(currentUser.getFirstname())
                                        .lastName(currentUser.getLastname())
                                        .build())
                                .specialist(UserShortDto.builder()
                                        .id(userToApplyAction.getId())
                                        .firstName(userToApplyAction.getFirstname())
                                        .lastName(userToApplyAction.getLastname())
                                        .build())
                                .manager(UserShortDto.builder()
                                        .id(value.getId())
                                        .firstName(value.getFirstname())
                                        .lastName(value.getLastname())
                                        .build())
                                .build())));
    }

    private void blockRoleMethodologist(User userToApplyAction) {
        List<User> allMethodologists = getUserList(UserListRequest.builder()
                .role(RoleType.METHODOLOGIST)
                .build())
                .stream()
                .filter(value -> !value.equals(userToApplyAction))
                .toList();
        if (allMethodologists.isEmpty()) {
            throw new BadRequestException("Невозможно заблокировать последнего методолога в системе");
        }
    }

    private UserProfileDetailsInfoRs buildUserProfileDetailsInfoRs(User user, UserProfile userProfile) {
        return UserProfileDetailsInfoRs.builder()
                .id(userProfile.getId())
                .lastName(user.getLastname())
                .firstName(user.getFirstname())
                .middleName(user.getMiddlename())
                .roles(user.getUserRoles().stream().map(userRoleMapper::buildUserRoleDto).collect(Collectors.toSet()))
                .activeRole(userProfile.getActiveRole() != null ?
                        userRoleMapper.buildUserRoleDto(userProfile.getActiveRole())
                        : userRoleMapper.buildUserRoleDto(user.getUserRoles().iterator().next()))
                .activeStatus(userProfile.getActiveStatus())
                .login(user.getLogin())
                .photo(fileRefMapper.toDto(userProfile.getPhoto()))
                .dateOfBirth(userProfile.getDateOfBirth())
                .workExperience(userProfile.getWorkExperience())
                .company(companyMapper.buildCompanyDto(userProfile.getCompany()))
                .build();
    }

    @Override
    public UserProfileDetailsInfoRs getUserProfileInfo(User user) {
        UserProfile userProfile = userProfileService.getUserProfileByUser(user);
        return buildUserProfileDetailsInfoRs(user, userProfile);
    }

    private User buildAddedUser(AddUserRq addUserRq) {
        return User.builder()
                .lastname(addUserRq.getLastName())
                .firstname(addUserRq.getFirstName())
                .middlename(addUserRq.getMiddleName())
                .login(addUserRq.getLogin())
                .build();
    }

    private Set<UserRole> createRoleSetForAddedUser(Set<RoleType> roles, User user) {
        return roles.stream().map(roleType -> UserRole.builder().user(user).roleType(roleType).build())
                .collect(Collectors.toSet());
    }

    private UserRole configureActiveUserRole(Set<UserRole> addedUserRoles) {
        if (addedUserRoles.isEmpty()) {
            throw new IllegalArgumentException("Набор ролей пользователя не может быть пустым");
        }
        Set<RoleType> userRoles = addedUserRoles.stream().map(UserRole::getRoleType).collect(Collectors.toSet());

        RoleType activeRole = RoleType.defaultRoleSettingByPriority(userRoles);

        return addedUserRoles.stream().filter(userRole -> userRole.getRoleType().equals(activeRole)).findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(RoleType.class,
                        "Не найдено корректного значения роли, для установки по умолчанию"));
    }
}