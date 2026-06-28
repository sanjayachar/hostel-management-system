package com.email.emailservice.service;

import com.email.emailservice.dto.EmailNotificationEvent;
import com.email.emailservice.entity.EmailNotification;
import com.email.emailservice.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private static final String STATUS_PENDING = "PENDING";

    private final EmailNotificationRepository emailNotificationRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Transactional
    public void process(EmailNotificationEvent event) {
        EmailNotification notification = save(event);

        if (notification == null) {
            return; // duplicate event
        }

        try {
            sendMail(notification);
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);
        } catch (Exception ex) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(ex.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
        }

        emailNotificationRepository.save(notification);
    }

    @Transactional
    public EmailNotification save(EmailNotificationEvent event) {
        if (event == null) {
            throw new RuntimeException("Email notification event is required.");
        }

        String eventId = event.eventId() == null || event.eventId().isBlank()
                ? UUID.randomUUID().toString()
                : event.eventId().trim();

        if (emailNotificationRepository.existsByEventId(eventId)) {
            return null;
        }

        EmailNotification notification = new EmailNotification();
        notification.setEventId(eventId);
        notification.setEventType(defaultValue(event.eventType(), "EMAIL_NOTIFICATION"));
        notification.setTemplateCode(defaultValue(event.templateCode(), "GENERAL"));
        notification.setRecipientUserId(event.recipientUserId());
        notification.setRecipientRole(blankToNull(event.recipientRole()));
        notification.setRecipientEmail(blankToNull(event.recipientEmail()));
        notification.setRecipientName(blankToNull(event.recipientName()));
        notification.setRecipientGroup(blankToNull(event.recipientGroup()));
        notification.setSubject(buildSubject(event));
        notification.setBody(buildBody(event));
        notification.setSourceService(blankToNull(event.sourceService()));
        notification.setSourceEntityType(blankToNull(event.sourceEntityType()));
        notification.setSourceEntityId(event.sourceEntityId());
        notification.setStatus(STATUS_PENDING);
        notification.setRetryCount(0);
        notification.setCreatedAt(event.occurredAt() == null ? LocalDateTime.now() : event.occurredAt());

        return emailNotificationRepository.save(notification);
    }

    private void sendMail(EmailNotification notification) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank()) {
            throw new RuntimeException("Recipient email is required.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipientEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());
        message.setFrom(senderEmail);
        javaMailSender.send(message);
    }

    private String buildSubject(EmailNotificationEvent event) {
        return switch (defaultValue(event.templateCode(), "GENERAL")) {
            case "USER_CREATED" -> "Your hostel account has been created";
            case "ACCOMMODATION_REQUEST_CREATED" -> "Accommodation request created";
            case "ACCOMMODATION_REQUEST_APPROVED" -> "Accommodation request approved";
            case "ACCOMMODATION_REQUEST_REJECTED" -> "Accommodation request rejected";
            case "CHAT_MENTIONED" -> "You were mentioned in hostel chat";
            default -> "Hostel notification";
        };
    }

    private String buildBody(EmailNotificationEvent event) {
        String recipientName = defaultValue(event.recipientName(), "User");
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(recipientName).append(",\n\n");
        body.append("You have a new hostel notification.\n\n");

        Map<String, String> variables = event.variables();
        if (variables != null && !variables.isEmpty()) {
            variables.forEach((key, value) -> body
                    .append(formatLabel(key))
                    .append(": ")
                    .append(value == null ? "" : value)
                    .append("\n"));
            body.append("\n");
        }

        body.append("Regards,\nHostel Management System");
        return body.toString();
    }

    private String formatLabel(String key) {
        if (key == null || key.isBlank()) {
            return "Detail";
        }

        String normalized = key.replace("_", " ").replace("-", " ").trim();
        return normalized.substring(0, 1).toUpperCase() + normalized.substring(1);
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
