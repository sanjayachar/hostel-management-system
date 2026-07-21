package com.auditlog.auditlogservice.entity;

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
@Table(name = "audit_logs", schema = "hostel")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long auditLogId;

    @Column(name = "event_id", nullable = false, unique = true, length = 80)
    private String eventId;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "level", nullable = false, length = 20)
    private String level;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "actor_username", length = 150)
    private String actorUsername;

    @Column(name = "actor_role", length = 80)
    private String actorRole;

    @Column(name = "class_name")
    private String className;

    @Column(name = "method_name", length = 150)
    private String methodName;

    @Column(name = "action_name", length = 150)
    private String actionName;

    @Column(name = "request_path")
    private String requestPath;

    @Column(name = "http_method", length = 20)
    private String httpMethod;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
