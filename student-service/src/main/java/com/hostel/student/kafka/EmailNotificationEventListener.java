package com.hostel.student.kafka;

import com.hostel.student.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailNotificationEventListener {

    private final EmailNotificationProducer emailNotificationProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EmailNotificationEvent event) {
        emailNotificationProducer.publish(event);
    }
}
