package com.hostel.chatservice.dto;

import java.util.List;

public record SendChatMessageRequest(
        String message,
        List<Long> mentionedUserIds
) {
}
