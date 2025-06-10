package com.ws.bebetter.repository;

import com.ws.bebetter.entity.SpecialistDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistDetails, UUID>,
        JpaSpecificationExecutor<SpecialistDetails> {
}
