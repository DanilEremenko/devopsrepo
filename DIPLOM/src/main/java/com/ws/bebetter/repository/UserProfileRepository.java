package com.ws.bebetter.repository;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @Query("select u from UserProfile u where u.user = :user")
    Optional<UserProfile> findByUser(User user);
}
