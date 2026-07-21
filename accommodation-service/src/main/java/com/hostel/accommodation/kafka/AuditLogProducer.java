package com.hostel.accommodation.kafka;

import com.hostel.accommodation.dto.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogProducer {
    private final KafkaTemplate<String, AuditLogEvent> kafkaTemplate;

    @Value("${hostel.audit.topic}")
    private String topic;

    public void publish(AuditLogEvent auditLogEvent) {
        try {
            kafkaTemplate.send(topic, auditLogEvent.eventId(), auditLogEvent)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Audit log publish failed for event {}", auditLogEvent.eventId(), ex);
                        }
                    });
        } catch (Exception ex) {
            log.warn("Audit log publish failed for event {}", auditLogEvent.eventId(), ex);
        }
    }
}
