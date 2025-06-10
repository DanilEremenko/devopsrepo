package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.Company;
import com.ws.bebetter.exception.ResourceAlreadyExistsException;
import com.ws.bebetter.repository.CompanyRepository;
import com.ws.bebetter.service.CompanyService;
import com.ws.bebetter.web.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public Company createNewCompany(CompanyDto companyDto) {
        companyRepository.findByInn(companyDto.getInn()).ifPresent(existingCompany -> {
            log.error("Duplicate company with INN: {}", companyDto.getInn());
            throw new ResourceAlreadyExistsException("Компания с ИНН %s уже существует."
                    .formatted(companyDto.getInn()));
        });

        Company company = Company.builder()
                .name(companyDto.getCompanyTitle())
                .inn(companyDto.getInn())
                .build();

        Company savedCompany = companyRepository.save(company);
        log.info("Company created with ID: {}", savedCompany.getId());
        return savedCompany;
    }

}
