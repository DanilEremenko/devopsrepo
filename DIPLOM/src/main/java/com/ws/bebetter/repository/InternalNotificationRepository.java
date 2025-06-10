package com.ws.bebetter.repository;

import com.ws.bebetter.entity.InternalNotification;
import com.ws.bebetter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface InternalNotificationRepository extends JpaRepository<InternalNotification, UUID>,
        JpaSpecificationExecutor<InternalNotification> {

    @Modifying
    @Query("update internal_notifications n set n.isRead = true, n.readTime = :readDateTime where n.recipient = :user")
    void markAllAsReadByUserWithReadTime(User user, LocalDateTime readDateTime);

    @Query("select (count(i) > 0) from internal_notifications i where i.recipient = :user")
    boolean existsByRecipient(User user);

}
