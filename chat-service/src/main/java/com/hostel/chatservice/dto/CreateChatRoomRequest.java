package com.hostel.chatservice.dto;

public record CreateChatRoomRequest(
        String roomName,
        String roomType
) {
}
