package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetActiveRoleRq {
    @NotNull(message = "Значение роли не может быть пустым.")
    @ValidationUtil.EnumConstraint(enumClass = RoleType.class, message = "Неподдерживаемое значение роли.")
    private RoleType activeRole;
}