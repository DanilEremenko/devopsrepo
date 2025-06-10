package com.ws.bebetter.web.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class InternalNotificationsListRq {

    @Min(value = 1, message = "Номер страницы должен быть больше или равен 1.")
    private Integer pageNumber = 1;

    @Min(value = 1, message = "Количество записей на страницу должно быть больше или равно 1.")
    private Integer countPerPage = 10;

}
