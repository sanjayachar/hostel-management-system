package com.hostel.student.dto;

import java.time.LocalDateTime;

public record AuditLogEvent(
        String eventId,
        LocalDateTime eventTime,
        String serviceName,
        String level,
        Long actorUserId,
        String actorUsername,
        String actorRole,
        String className,
        String methodName,
        String actionName,
        String requestPath,
        String httpMethod,
        String status,
        String message,
        String errorMessage,
        Long durationMs
) {
}
