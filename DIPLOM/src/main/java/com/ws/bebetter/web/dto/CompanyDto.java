package com.ws.bebetter.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class CompanyDto {

    @NotNull(message = "Значение названия компании не должно быть пустым.")
    @Length(min = 1, max = 100, message = "Длина имени компании должна составлять от 1 до 100 символов.")
    private String companyTitle;

    @NotNull(message = "Значение ИНН компании не должно быть пустым.")
    @Length(min = 10, max = 12,
            message = "Длина инн компании должна составлять от 10 до 12 символов в зависимости от типа компании.")
    private String inn;

}
