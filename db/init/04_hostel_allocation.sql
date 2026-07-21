CREATE SCHEMA IF NOT EXISTS hostel;

ALTER TABLE hostel.requests
ADD COLUMN IF NOT EXISTS decision_note VARCHAR(500),
ADD COLUMN IF NOT EXISTS decided_by VARCHAR(150),
ADD COLUMN IF NOT EXISTS decided_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS hostel.hostels (
    hostel_id BIGSERIAL PRIMARY KEY,
    hostel_code VARCHAR(40) NOT NULL UNIQUE,
    hostel_name VARCHAR(150) NOT NULL,
    hostel_type VARCHAR(40) NOT NULL,
    address VARCHAR(500),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(255),
    modified_at TIMESTAMP,
    active_flag VARCHAR(255) DEFAULT 'Y'
);

CREATE TABLE IF NOT EXISTS hostel.hostel_rooms (
    room_id BIGSERIAL PRIMARY KEY,
    hostel_id BIGINT NOT NULL REFERENCES hostel.hostels(hostel_id),
    room_number VARCHAR(40) NOT NULL,
    floor_number INTEGER,
    room_type VARCHAR(60) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    occupied_count INTEGER NOT NULL DEFAULT 0 CHECK (occupied_count >= 0),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(255),
    modified_at TIMESTAMP,
    active_flag VARCHAR(255) DEFAULT 'Y',
    CONSTRAINT uk_hostel_room_number UNIQUE (hostel_id, room_number),
    CONSTRAINT chk_room_occupied_capacity CHECK (occupied_count <= capacity)
);

CREATE TABLE IF NOT EXISTS hostel.room_allocations (
    allocation_id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL REFERENCES hostel.requests(request_id),
    hostel_id BIGINT NOT NULL REFERENCES hostel.hostels(hostel_id),
    room_id BIGINT NOT NULL REFERENCES hostel.hostel_rooms(room_id),
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
    CONSTRAINT uk_room_allocation_request UNIQUE (request_id)
);

CREATE INDEX IF NOT EXISTS idx_hostel_rooms_hostel_id
ON hostel.hostel_rooms (hostel_id);

CREATE INDEX IF NOT EXISTS idx_room_allocations_room_status
ON hostel.room_allocations (room_id, allocation_status);

CREATE INDEX IF NOT EXISTS idx_room_allocations_user
ON hostel.room_allocations (user_id, user_role);
