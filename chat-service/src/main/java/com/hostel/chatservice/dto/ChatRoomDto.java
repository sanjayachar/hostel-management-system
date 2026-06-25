package com.hostel.chatservice.dto;

import java.time.LocalDateTime;

public record ChatRoomDto(
        Long roomId,
        String roomName,
        String roomType,
        Long createdBy,
        LocalDateTime createdAt
) {
}
