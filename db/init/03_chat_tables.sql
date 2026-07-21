CREATE SCHEMA IF NOT EXISTS hostel;

CREATE TABLE IF NOT EXISTS hostel.chat_rooms (
    room_id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(150) NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hostel.chat_room_members (
    room_member_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES hostel.chat_rooms(room_id),
    user_id BIGINT NOT NULL,
    username VARCHAR(150),
    user_role VARCHAR(80),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_chat_room_member_user UNIQUE (room_id, user_id)
);

CREATE TABLE IF NOT EXISTS hostel.chat_messages (
    message_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES hostel.chat_rooms(room_id),
    sender_user_id BIGINT NOT NULL,
    sender_username VARCHAR(150),
    sender_role VARCHAR(80),
    message TEXT NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_room_members_user_id
ON hostel.chat_room_members (user_id);

CREATE INDEX IF NOT EXISTS idx_chat_messages_room_created_at
ON hostel.chat_messages (room_id, created_at);

CREATE UNIQUE INDEX IF NOT EXISTS uk_chat_rooms_help_desk
ON hostel.chat_rooms (room_type)
WHERE room_type = 'HELP_DESK';

INSERT INTO hostel.chat_rooms (room_name, room_type)
SELECT 'Hostel Help Desk', 'HELP_DESK'
WHERE NOT EXISTS (
    SELECT 1 FROM hostel.chat_rooms WHERE room_type = 'HELP_DESK'
);
