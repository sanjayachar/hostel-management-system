package com.email.emailservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications", schema = "hostel")
@Getter
@Setter
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_notification_id")
    private Long emailNotificationId;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "template_code", nullable = false, length = 100)
    private String templateCode;

    @Column(name = "recipient_user_id")
    private Long recipientUserId;

    @Column(name = "recipient_role", length = 80)
    private String recipientRole;

    @Column(name = "recipient_email", length = 180)
    private String recipientEmail;

    @Column(name = "recipient_name", length = 180)
    private String recipientName;

    @Column(name = "recipient_group", length = 80)
    private String recipientGroup;

    @Column(name = "subject", length = 250)
    private String subject;

    @Column(name = "body", columnDefinition = "text")
    private String body;

    @Column(name = "source_service", length = 100)
    private String sourceService;

    @Column(name = "source_entity_type", length = 100)
    private String sourceEntityType;

    @Column(name = "source_entity_id")
    private Long sourceEntityId;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
