package com.hostel.chatservice.dto;

import com.hostel.chatservice.security.UserPrincipal;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public record ChatUser(Long userId, String username, String role) {

    public static ChatUser fromPrincipal(Principal principal) {
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return new ChatUser(userPrincipal.getUserId(), userPrincipal.getUsername(), userPrincipal.getRole());
        }

        throw new IllegalStateException("Authenticated chat user is required.");
    }
}
