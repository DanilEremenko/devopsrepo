package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {

    @NotNull
    private UUID id;

    @ValidationUtil.EnumConstraint(enumClass = RoleType.class, message = "Неподдерживаемое значение роли")
    private RoleType roleType;

}
