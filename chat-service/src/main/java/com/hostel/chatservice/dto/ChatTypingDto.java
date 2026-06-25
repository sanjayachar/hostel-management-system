package com.hostel.chatservice.dto;

import java.time.LocalDateTime;

public record ChatTypingDto(
        Long roomId,
        Long senderUserId,
        String senderUsername,
        String senderRole,
        Boolean typing,
        LocalDateTime eventTime
) {
}
