package com.hostel.chatservice.service;

import com.hostel.chatservice.dto.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserDisplayNameService {

    private final ChatUserProfileService chatUserProfileService;

    public String getDisplayName(ChatUser user) {
        return resolveDisplayName(user.userId(), user.username(), user.role());
    }

    public String resolveDisplayName(Long userId, String fallbackName, String role) {
        return chatUserProfileService.resolveDisplayName(userId, fallbackName, role);
    }
}
