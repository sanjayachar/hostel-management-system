package com.email.emailservice.kafka;

import com.email.emailservice.dto.EmailNotificationEvent;
import com.email.emailservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "${hostel.email.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(EmailNotificationEvent event) {
        try {
            emailNotificationService.process(event);
        } catch (Exception ex) {
            log.error("Failed to save email notification event {}", event.eventId(), ex);
        }
    }
}
