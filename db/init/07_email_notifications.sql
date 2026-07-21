CREATE SCHEMA IF NOT EXISTS hostel;

CREATE TABLE IF NOT EXISTS hostel.email_notifications (
    email_notification_id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL UNIQUE,
    event_type VARCHAR(80) NOT NULL,
    template_code VARCHAR(100) NOT NULL,
    recipient_user_id BIGINT,
    recipient_role VARCHAR(80),
    recipient_email VARCHAR(180),
    recipient_name VARCHAR(180),
    recipient_group VARCHAR(80),
    subject VARCHAR(250),
    body TEXT,
    source_service VARCHAR(100),
    source_entity_type VARCHAR(100),
    source_entity_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_email_notifications_status
ON hostel.email_notifications (status, created_at);

CREATE INDEX IF NOT EXISTS idx_email_notifications_recipient
ON hostel.email_notifications (recipient_user_id, created_at);
