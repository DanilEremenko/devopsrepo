package com.ws.bebetter.web.mapper;

import com.ws.bebetter.entity.Company;
import com.ws.bebetter.web.dto.CompanyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends Mappable<Company, CompanyDto> {
    default CompanyDto buildCompanyDto(Company company) {
        if (company == null) {
            throw new RuntimeException();
        }
        return CompanyDto.builder()
                .companyTitle(company.getName())
                .inn(company.getInn())
                .build();
    }
}