package com.hostel.chatservice.dto;

public record ChatMentionDto(
        Long mentionId,
        Long userId,
        String username,
        String role,
        Boolean readStatus
) {
}
