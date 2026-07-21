CREATE SCHEMA IF NOT EXISTS hostel;

CREATE TABLE IF NOT EXISTS hostel.audit_logs (
    audit_log_id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(80) NOT NULL UNIQUE,
    event_time TIMESTAMP NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    level VARCHAR(20) NOT NULL,
    actor_user_id BIGINT,
    actor_username VARCHAR(150),
    actor_role VARCHAR(80),
    class_name VARCHAR(255),
    method_name VARCHAR(150),
    action_name VARCHAR(150),
    request_path VARCHAR(255),
    http_method VARCHAR(20),
    status VARCHAR(30) NOT NULL,
    message TEXT,
    error_message TEXT,
    duration_ms BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_event_time
ON hostel.audit_logs (event_time DESC);

CREATE INDEX IF NOT EXISTS idx_audit_logs_service_name
ON hostel.audit_logs (service_name);

CREATE INDEX IF NOT EXISTS idx_audit_logs_level
ON hostel.audit_logs (level);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor_username
ON hostel.audit_logs (actor_username);
