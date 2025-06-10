package com.ws.bebetter.web.mapper;

import com.ws.bebetter.entity.UserRole;
import com.ws.bebetter.web.dto.UserRoleDto;
import org.mapstruct.Mapper;

/**
 * Маппер для работы с UserRole
 */
@Mapper(componentModel = "spring")
public interface UserRoleMapper extends Mappable<UserRole, UserRoleDto> {

    default UserRoleDto buildUserRoleDto(UserRole userRole) {
        return UserRoleDto.builder()
                .id(userRole.getId())
                .roleType(userRole.getRoleType())
                .build();
    }
}
