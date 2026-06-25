package com.hostel.student.kafka;

import com.hostel.student.dto.EmailNotificationApplicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailNotificationEventListener {

    private final EmailNotificationProducer emailNotificationProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EmailNotificationApplicationEvent event) {
        emailNotificationProducer.publish(event.event());
    }
}
