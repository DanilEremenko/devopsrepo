package com.ws.bebetter.repository;

import com.ws.bebetter.entity.ManagerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerDetails, UUID>,
        JpaSpecificationExecutor<ManagerDetails> {
}
