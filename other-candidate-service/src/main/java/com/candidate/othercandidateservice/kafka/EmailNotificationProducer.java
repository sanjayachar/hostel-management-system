package com.candidate.othercandidateservice.kafka;

import com.candidate.othercandidateservice.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationProducer {

    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    @Value("${hostel.email.topic}")
    private String topic;

    public void publish(EmailNotificationEvent event) {
        kafkaTemplate.send(topic, event.eventId(), event)
                .whenComplete((result, ex)->{
                    if (ex != null) {
                        log.warn("Email event publish failed {}", event.eventId(), ex);
                    }
                });
    }
}
