package com.email.emailservice.repository;

import com.email.emailservice.entity.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {

    boolean existsByEventId(String eventId);
    Optional<EmailNotification> findByEventId(String eventId);
}
