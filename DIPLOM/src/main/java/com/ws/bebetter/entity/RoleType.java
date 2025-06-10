package com.ws.bebetter.entity;

import lombok.Getter;

import java.util.Comparator;
import java.util.Set;

@Getter
public enum RoleType {

    SPECIALIST(1),
    METHODOLOGIST(2),
    MENTOR(3),
    MANAGER(4);

    private final Integer priority;

    RoleType(Integer priority) {
        this.priority = priority;
    }

    public static RoleType defaultRoleSettingByPriority(Set<RoleType> userRoles) {
        return userRoles.stream().min(Comparator.comparingInt(userRole -> userRole.priority)).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("За пользователем не закреплено ни одной роли"));
    }
}
