package com.hostel.accommodation.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record EmailNotificationEvent(
        String eventId,
        String eventType,
        String templateCode,
        Long recipientUserId,
        String recipientRole,
        String recipientEmail,
        String recipientName,
        String recipientGroup,
        String sourceService,
        String sourceEntityType,
        Long sourceEntityId,
        Map<String, String> variables,
        LocalDateTime occurredAt
) {
}
