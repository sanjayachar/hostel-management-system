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
            case "ACCOMMODATION_REQUEST_REVIEW_REQUIRED" -> "Accommodation request requires review";
            case "ACCOMMODATION_REQUEST_APPROVED" -> "Accommodation request approved";
            case "ACCOMMODATION_REQUEST_REJECTED" -> "Accommodation request rejected";
            case "CHAT_MENTIONED" -> "You were mentioned in hostel chat";
            default -> "Hostel notification";
        };
    }

    private String buildBody(EmailNotificationEvent event) {
        String templateCode = defaultValue(event.templateCode(), "GENERAL");
        if ("ACCOMMODATION_REQUEST_REVIEW_REQUIRED".equals(templateCode)) {
            return buildAdminAccommodationRequestBody(event);
        }

        if ("ACCOMMODATION_REQUEST_CREATED".equals(templateCode)) {
            return buildRequesterAccommodationRequestBody(event);
        }

        if ("ACCOMMODATION_REQUEST_APPROVED".equals(templateCode)) {
            return buildApprovedAccommodationRequestBody(event);
        }

        if ("ACCOMMODATION_REQUEST_REJECTED".equals(templateCode)) {
            return buildRejectedAccommodationRequestBody(event);
        }

        if ("CHAT_MENTIONED".equals(templateCode)) {
            return buildChatMentionedBody(event);
        }

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

    private String buildChatMentionedBody(EmailNotificationEvent event) {
        Map<String, String> variables = event.variables();
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(defaultValue(event.recipientName(), "User")).append(",\n\n");
        body.append("You were mentioned in hostel chat.\n\n");
        appendVariable(body, variables, "roomName", "Room");
        appendVariable(body, variables, "senderName", "Mentioned By");
        appendVariable(body, variables, "senderRole", "Sender Role");
        appendVariable(body, variables, "messagePreview", "Message");
        body.append("\nPlease open the hostel application to reply.\n\n");
        body.append("Regards,\nHostel Management System");

        return body.toString();
    }

    private String buildApprovedAccommodationRequestBody(EmailNotificationEvent event) {
        Map<String, String> variables = event.variables();
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(defaultValue(event.recipientName(), "Requester")).append(",\n\n");
        body.append("Your accommodation request has been approved and the accommodation has been allotted.\n\n");
        appendVariable(body, variables, "requestId", "Request ID");
        appendVariable(body, variables, "requestType", "Request Type");
        appendVariable(body, variables, "hostelName", "Hostel");
        appendVariable(body, variables, "roomNumber", "Room");
        appendVariable(body, variables, "bedNumber", "Bed");
        appendVariable(body, variables, "fromDate", "Allocated From");
        appendVariable(body, variables, "toDate", "Allocated To");
        appendVariable(body, variables, "allocationNote", "Allocation Note");
        appendVariable(body, variables, "decisionNote", "Decision Note");
        body.append("\nRegards,\nHostel Management System");

        return body.toString();
    }

    private String buildRejectedAccommodationRequestBody(EmailNotificationEvent event) {
        Map<String, String> variables = event.variables();
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(defaultValue(event.recipientName(), "Requester")).append(",\n\n");
        body.append("Your accommodation request has been rejected.\n\n");
        appendVariable(body, variables, "requestId", "Request ID");
        appendVariable(body, variables, "requestType", "Request Type");
        appendVariable(body, variables, "fromDate", "From Date");
        appendVariable(body, variables, "toDate", "To Date");
        appendVariable(body, variables, "noOfPersons", "No. of Persons");
        appendVariable(body, variables, "decisionNote", "Decision Note");
        body.append("\nRegards,\nHostel Management System");

        return body.toString();
    }

    private String buildAdminAccommodationRequestBody(EmailNotificationEvent event) {
        Map<String, String> variables = event.variables();
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(defaultValue(event.recipientName(), "Admin")).append(",\n\n");
        body.append("A new accommodation request has been created. These are the accommodation request details. Please review it.\n\n");
        appendVariable(body, variables, "requestId", "Request ID");
        appendVariable(body, variables, "requestType", "Request Type");
        appendVariable(body, variables, "requesterCode", "Requester Code");
        appendVariable(body, variables, "requesterName", "Requester Name");
        appendVariable(body, variables, "fromDate", "From Date");
        appendVariable(body, variables, "toDate", "To Date");
        appendVariable(body, variables, "noOfPersons", "No. of Persons");
        appendVariable(body, variables, "status", "Status");
        body.append("\nRegards,\nHostel Management System");

        return body.toString();
    }

    private String buildRequesterAccommodationRequestBody(EmailNotificationEvent event) {
        Map<String, String> variables = event.variables();
        StringBuilder body = new StringBuilder();

        body.append("Hi ").append(defaultValue(event.recipientName(), "Requester")).append(",\n\n");
        body.append("Your accommodation request has been created successfully. The admin team will review it.\n\n");
        appendVariable(body, variables, "requestId", "Request ID");
        appendVariable(body, variables, "requestType", "Request Type");
        appendVariable(body, variables, "fromDate", "From Date");
        appendVariable(body, variables, "toDate", "To Date");
        appendVariable(body, variables, "noOfPersons", "No. of Persons");
        appendVariable(body, variables, "status", "Status");
        body.append("\nRegards,\nHostel Management System");

        return body.toString();
    }

    private void appendVariable(StringBuilder body, Map<String, String> variables, String key, String label) {
        if (variables == null || variables.isEmpty()) {
            return;
        }

        String value = variables.get(key);
        if (value == null || value.isBlank()) {
            return;
        }

        body.append(label).append(": ").append(value).append("\n");
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
