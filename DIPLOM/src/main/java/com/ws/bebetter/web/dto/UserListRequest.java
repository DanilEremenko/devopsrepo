package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequest {

    private String lastname;

    @ValidationUtil.EnumConstraint(enumClass = RoleType.class, message = "Неподдерживаемое значение роли.")
    private RoleType role;

    @Pattern(regexp = "lastname|registrationDate|updateDate|activeStatus",
            message = "Поле сортировки должно иметь значение 'lastname', 'registrationDate', 'updateDate' " +
                    "или 'activeStatus'")
    @Builder.Default
    private String sortBy = "lastname";

    @Pattern(regexp = "asc|desc", message = "Порядок сортировки должен иметь значение 'asc' или 'desc'")
    @Builder.Default
    private String orderSort = "asc";

    @Min(value = 1, message = "Номер страницы должен быть больше или равен 1.")
    @Builder.Default
    private Integer pageNumber = 1;

    @Min(value = 1, message = "Количество записей на страницу должно быть больше или равно 1.")
    @Builder.Default
    private Integer countPerPage = 30;

}


