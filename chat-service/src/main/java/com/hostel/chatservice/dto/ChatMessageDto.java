package com.hostel.chatservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageDto(
        Long messageId,
        Long roomId,
        Long senderUserId,
        String senderUsername,
        String senderRole,
        String message,
        List<ChatMentionDto> mentions,
        LocalDateTime createdAt
) {
}
