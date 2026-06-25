package com.hostel.chatservice.dto;

public record ChatRoomMemberDto(
        Long userId,
        String username,
        String userRole
) {
}
