package com.ws.bebetter.service;

import com.ws.bebetter.entity.Company;
import com.ws.bebetter.web.dto.CompanyDto;

/**
 * Сервис для управления компаниями.
 */
public interface CompanyService {

    /**
     * Создает новую компанию.
     *
     * @param companyDto объект, содержащий информацию о регистрируемой компании.
     * @return {@link Company} если компания успешно создана.
     * @throws com.ws.bebetter.exception.ResourceAlreadyExistsException если компания уже существует.
     */
    Company createNewCompany(CompanyDto companyDto);

}
