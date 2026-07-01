package com.hostel.chatservice.dto;

public record ChatUserProfileDto(
        Long userId,
        String displayName,
        String role,
        String email
) {
}
