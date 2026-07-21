CREATE SCHEMA IF NOT EXISTS hostel;

CREATE TABLE IF NOT EXISTS hostel.chat_message_mentions (
    mention_id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL REFERENCES hostel.chat_messages(message_id),
    room_id BIGINT NOT NULL REFERENCES hostel.chat_rooms(room_id),
    mentioned_user_id BIGINT NOT NULL,
    mentioned_username VARCHAR(150),
    mentioned_role VARCHAR(80),
    read_status BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_chat_message_mention UNIQUE (message_id, mentioned_user_id)
);

CREATE INDEX IF NOT EXISTS idx_chat_message_mentions_user
ON hostel.chat_message_mentions (mentioned_user_id, read_status, created_at);

CREATE INDEX IF NOT EXISTS idx_chat_message_mentions_room
ON hostel.chat_message_mentions (room_id, created_at);
