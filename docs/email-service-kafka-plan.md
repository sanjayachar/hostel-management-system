# Email Service Kafka Plan

## Goal

Email must be asynchronous. Student/staff/candidate creation, accommodation request creation, request approval/rejection, and chat mentions should publish notification events to Kafka. A dedicated `email-service` consumes those events, sends email, and saves send status in `hostel.email_notifications`.

## Topic

Use one topic for email notifications:

```yaml
hostel:
  email:
    topic: hostel.email.notifications
```

Producer services:

- `student-service`
- `staff-service`
- `other-candidate-service`
- `accommodation-service`
- `chat-service`

Consumer service:

- `email-service`

## Event DTO

Create the same record in producer services and email-service:

```java
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
```

Recommended template codes:

- `USER_CREATED`
- `ACCOMMODATION_REQUEST_CREATED`
- `ACCOMMODATION_REQUEST_APPROVED`
- `ACCOMMODATION_REQUEST_REJECTED`
- `CHAT_MENTIONED`

## Publish Points

Publish after DB commit. Prefer:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
```

This prevents emails from being sent when the database transaction rolls back.

Publish locations:

- `StudentsService.registerStudent` after student save.
- `StaffService.registerStaff` after staff save.
- `CandidateService.registerCandidate` after candidate save.
- `AccommodationService.saveAccommodationRequest` after request save.
- `AccommodationService.decideAccommodationRequest` after approve/reject save.
- `ChatService.sendMessage` after mentions are saved. A TODO comment is already placed there for `CHAT_MENTIONED`.

## Email Service Flow

1. Kafka listener consumes `EmailNotificationEvent`.
2. Check `event_id` in `hostel.email_notifications`.
3. If already present as `SENT`, skip it for idempotency.
4. Build subject/body from `templateCode` and `variables`.
5. Insert or update row as `PENDING`.
6. Send email using `JavaMailSender`.
7. Update row to `SENT` with `sent_at`, or `FAILED` with `error_message` and `retry_count`.

## Table

The table script is in:

```text
db/init/07_email_notifications.sql
```
