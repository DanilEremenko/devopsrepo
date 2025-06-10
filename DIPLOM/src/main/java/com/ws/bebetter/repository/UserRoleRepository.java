package com.ws.bebetter.repository;

import com.ws.bebetter.entity.RoleType;
import com.ws.bebetter.entity.User;
import com.ws.bebetter.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    @Query("select u from user_roles u where u.user = :user and u.roleType = :roleType")
    Optional<UserRole> findByUserAndRoleType(User user, RoleType roleType);
}