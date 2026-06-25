package com.auditlog.auditlogservice.kafka;

import com.auditlog.auditlogservice.dto.AuditLogEvent;
import com.auditlog.auditlogservice.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogConsumer {

    private final AuditLogService auditLogService;

    @KafkaListener(topics = "${hostel.audit.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(AuditLogEvent event) {
        try {
            auditLogService.save(event);
        } catch (Exception ex) {
            log.error("Failed to save audit log event {}", event.eventId(), ex);
        }
    }
}
