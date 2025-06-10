package com.ws.bebetter.repository;

import com.ws.bebetter.entity.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {

    @Query("select p from PasswordRecovery p where p.user.id = :userId")
    List<PasswordRecovery> findAllByUserId(UUID userId);

}
