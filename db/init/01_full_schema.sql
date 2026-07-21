CREATE SCHEMA IF NOT EXISTS hostel;

CREATE TABLE IF NOT EXISTS hostel.roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active_flag CHAR(1) DEFAULT 'Y',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS roles_role_name_key
ON hostel.roles (role_name);

CREATE TABLE IF NOT EXISTS hostel.users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    account_type VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    failed_attempts INTEGER DEFAULT 0,
    token_version INTEGER DEFAULT 0,
    last_login_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    active_flag VARCHAR(255) NOT NULL DEFAULT 'Y',
    password_change_required BOOLEAN NOT NULL DEFAULT FALSE,
    password_changed_at TIMESTAMP,
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES hostel.roles (role_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS users_username_key
ON hostel.users (username);

CREATE TABLE IF NOT EXISTS hostel.students (
    student_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    admission_number VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    gender CHAR(1),
    date_of_birth DATE,
    contact_number VARCHAR(20),
    personal_email VARCHAR(255),
    father_name VARCHAR(120),
    mother_name VARCHAR(120),
    address TEXT,
    hostel_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    created_by VARCHAR(20) NOT NULL,
    modified_by VARCHAR(20) NOT NULL,
    active_flag CHAR(1) NOT NULL DEFAULT 'Y',
    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id) REFERENCES hostel.users (user_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS students_user_id_key
ON hostel.students (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS students_admission_number_key
ON hostel.students (admission_number);

CREATE TABLE IF NOT EXISTS hostel.student_documents (
    document_id SERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_documents_student
        FOREIGN KEY (student_id) REFERENCES hostel.students (student_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.student_images (
    image_id SERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    image_type VARCHAR(50),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_images_student
        FOREIGN KEY (student_id) REFERENCES hostel.students (student_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.staff (
    staff_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    employee_code VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    gender CHAR(1),
    date_of_birth DATE,
    contact_number VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    designation VARCHAR(120),
    department VARCHAR(120),
    date_of_joining DATE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    created_by VARCHAR(20) NOT NULL,
    modified_by VARCHAR(20) NOT NULL,
    active_flag CHAR(1) NOT NULL DEFAULT 'Y',
    CONSTRAINT fk_staff_user
        FOREIGN KEY (user_id) REFERENCES hostel.users (user_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS staff_user_id_key
ON hostel.staff (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS staff_employee_code_key
ON hostel.staff (employee_code);

CREATE TABLE IF NOT EXISTS hostel.staff_documents (
    document_id SERIAL PRIMARY KEY,
    staff_id BIGINT,
    document_type VARCHAR(50),
    document_number VARCHAR(100),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_documents_staff
        FOREIGN KEY (staff_id) REFERENCES hostel.staff (staff_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.staff_images (
    image_id SERIAL PRIMARY KEY,
    staff_id BIGINT,
    image_type VARCHAR(50),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_images_staff
        FOREIGN KEY (staff_id) REFERENCES hostel.staff (staff_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.candidates (
    candidate_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    gender CHAR(1),
    date_of_birth DATE,
    email VARCHAR(255),
    contact_number VARCHAR(20),
    address TEXT,
    city VARCHAR(120),
    state VARCHAR(120),
    pin_code VARCHAR(20),
    applied_post VARCHAR(120),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    created_by VARCHAR(20) NOT NULL,
    modified_by VARCHAR(20) NOT NULL,
    active_flag CHAR(1) NOT NULL DEFAULT 'Y',
    candidate_code VARCHAR(255) NOT NULL,
    CONSTRAINT fk_candidates_user
        FOREIGN KEY (user_id) REFERENCES hostel.users (user_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS candidates_user_id_key
ON hostel.candidates (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS candidates_candidate_code_key
ON hostel.candidates (candidate_code);

CREATE TABLE IF NOT EXISTS hostel.candidate_documents (
    document_id SERIAL PRIMARY KEY,
    candidate_id BIGINT,
    document_type VARCHAR(50),
    document_number VARCHAR(100),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_documents_candidate
        FOREIGN KEY (candidate_id) REFERENCES hostel.candidates (candidate_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.candidate_images (
    image_id SERIAL PRIMARY KEY,
    candidate_id BIGINT,
    image_type VARCHAR(50),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_images_candidate
        FOREIGN KEY (candidate_id) REFERENCES hostel.candidates (candidate_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.requests (
    request_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(255) NOT NULL,
    reason VARCHAR(255),
    from_date DATE,
    to_date DATE,
    no_of_days INTEGER,
    no_of_persons INTEGER,
    status VARCHAR(255) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    active_flag VARCHAR(255) NOT NULL DEFAULT 'Y',
    user_role VARCHAR(255),
    modified_at TIMESTAMP,
    decision_note VARCHAR(255),
    decided_by VARCHAR(255),
    decided_at TIMESTAMP,
    CONSTRAINT fk_requests_user
        FOREIGN KEY (user_id) REFERENCES hostel.users (user_id)
);

CREATE TABLE IF NOT EXISTS hostel.guest_details (
    guest_id SERIAL PRIMARY KEY,
    request_id BIGINT,
    guest_name VARCHAR(120),
    relation VARCHAR(50),
    age INTEGER,
    gender CHAR(1),
    CONSTRAINT fk_guest_details_request
        FOREIGN KEY (request_id) REFERENCES hostel.requests (request_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.guest_proofs (
    proof_id SERIAL PRIMARY KEY,
    guest_id INTEGER,
    proof_type VARCHAR(50),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guest_proofs_guest
        FOREIGN KEY (guest_id) REFERENCES hostel.guest_details (guest_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hostel.hostels (
    hostel_id BIGSERIAL PRIMARY KEY,
    hostel_code VARCHAR(40) NOT NULL,
    hostel_name VARCHAR(150) NOT NULL,
    hostel_type VARCHAR(40) NOT NULL,
    address VARCHAR(500),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(255),
    modified_at TIMESTAMP,
    active_flag VARCHAR(255) DEFAULT 'Y'
);

CREATE UNIQUE INDEX IF NOT EXISTS hostels_hostel_code_key
ON hostel.hostels (hostel_code);

CREATE TABLE IF NOT EXISTS hostel.hostel_rooms (
    room_id BIGSERIAL PRIMARY KEY,
    hostel_id BIGINT NOT NULL,
    room_number VARCHAR(40) NOT NULL,
    floor_number INTEGER,
    room_type VARCHAR(60) NOT NULL,
    capacity INTEGER NOT NULL,
    occupied_count INTEGER NOT NULL DEFAULT 0,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(255),
    modified_at TIMESTAMP,
    active_flag VARCHAR(255) DEFAULT 'Y',
    CONSTRAINT fk_hostel_rooms_hostel
        FOREIGN KEY (hostel_id) REFERENCES hostel.hostels (hostel_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_hostel_room_number
ON hostel.hostel_rooms (hostel_id, room_number);

CREATE INDEX IF NOT EXISTS idx_hostel_rooms_hostel_id
ON hostel.hostel_rooms (hostel_id);

CREATE TABLE IF NOT EXISTS hostel.room_allocations (
    allocation_id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    hostel_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_role VARCHAR(40) NOT NULL,
    allocated_from DATE NOT NULL,
    allocated_to DATE NOT NULL,
    bed_number VARCHAR(40),
    allocation_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    allocation_note VARCHAR(500),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(255),
    modified_at TIMESTAMP,
    active_flag VARCHAR(255) DEFAULT 'Y',
    CONSTRAINT fk_room_allocations_request
        FOREIGN KEY (request_id) REFERENCES hostel.requests (request_id),
    CONSTRAINT fk_room_allocations_hostel
        FOREIGN KEY (hostel_id) REFERENCES hostel.hostels (hostel_id),
    CONSTRAINT fk_room_allocations_room
        FOREIGN KEY (room_id) REFERENCES hostel.hostel_rooms (room_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_room_allocation_request
ON hostel.room_allocations (request_id);

CREATE INDEX IF NOT EXISTS idx_room_allocations_room_status
ON hostel.room_allocations (room_id, allocation_status);

CREATE INDEX IF NOT EXISTS idx_room_allocations_user
ON hostel.room_allocations (user_id, user_role);

CREATE TABLE IF NOT EXISTS hostel.chat_rooms (
    room_id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(150),
    room_type VARCHAR(30) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hostel.chat_messages (
    message_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    sender_username VARCHAR(150),
    sender_role VARCHAR(80),
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_status BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_chat_messages_room
        FOREIGN KEY (room_id) REFERENCES hostel.chat_rooms (room_id)
);

CREATE TABLE IF NOT EXISTS hostel.chat_room_members (
    room_member_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(150),
    user_role VARCHAR(80),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_room_members_room
        FOREIGN KEY (room_id) REFERENCES hostel.chat_rooms (room_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_chat_room_member_user
ON hostel.chat_room_members (room_id, user_id);

CREATE TABLE IF NOT EXISTS hostel.chat_message_mentions (
    mention_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) NOT NULL,
    mentioned_role VARCHAR(80),
    mentioned_user_id BIGINT NOT NULL,
    mentioned_username VARCHAR(150),
    read_at TIMESTAMP(6),
    read_status BOOLEAN,
    message_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    CONSTRAINT fk_chat_message_mentions_message
        FOREIGN KEY (message_id) REFERENCES hostel.chat_messages (message_id),
    CONSTRAINT fk_chat_message_mentions_room
        FOREIGN KEY (room_id) REFERENCES hostel.chat_rooms (room_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_chat_message_mention
ON hostel.chat_message_mentions (message_id, mentioned_user_id);

CREATE INDEX IF NOT EXISTS idx_chat_message_mentions_user
ON hostel.chat_message_mentions (mentioned_user_id, read_status, created_at);

CREATE INDEX IF NOT EXISTS idx_chat_message_mentions_room
ON hostel.chat_message_mentions (room_id, created_at);

CREATE TABLE IF NOT EXISTS hostel.audit_logs (
    audit_log_id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(80) NOT NULL,
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

CREATE UNIQUE INDEX IF NOT EXISTS audit_logs_event_id_key
ON hostel.audit_logs (event_id);

CREATE INDEX IF NOT EXISTS idx_audit_logs_event_time
ON hostel.audit_logs (event_time);

CREATE INDEX IF NOT EXISTS idx_audit_logs_service_name
ON hostel.audit_logs (service_name);

CREATE INDEX IF NOT EXISTS idx_audit_logs_level
ON hostel.audit_logs (level);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor_username
ON hostel.audit_logs (actor_username);

CREATE TABLE IF NOT EXISTS hostel.email_notifications (
    email_notification_id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
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
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS email_notifications_event_id_key
ON hostel.email_notifications (event_id);

CREATE INDEX IF NOT EXISTS idx_email_notifications_status
ON hostel.email_notifications (status, created_at);

CREATE INDEX IF NOT EXISTS idx_email_notifications_recipient
ON hostel.email_notifications (recipient_user_id, created_at);
