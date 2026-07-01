package com.hostel.chatservice.service;

import com.hostel.chatservice.client.grpc.UserProfileClient;
import com.hostel.chatservice.dto.ChatUserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatUserProfileService {

    private final UserProfileClient userProfileClient;

    public Optional<ChatUserProfileDto> findProfile(Long userId, String role) {
        return userProfileClient.getProfile(userId, role);
    }

    public Optional<ChatUserProfileDto> findProfile(Long userId) {
        return userProfileClient.getProfile(userId);
    }

    public List<ChatUserProfileDto> findAllProfiles() {
        return userProfileClient.listProfiles();
    }

    public String resolveDisplayName(Long userId, String fallbackName, String role) {
        return findProfile(userId, role)
                .map(ChatUserProfileDto::displayName)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> normalizeFallback(fallbackName));
    }

    private String normalizeFallback(String fallbackName) {
        if (fallbackName == null || fallbackName.isBlank()) {
            return "User";
        }

        if ("admin".equalsIgnoreCase(fallbackName)) {
            return "Admin";
        }

        return fallbackName.trim();
    }
}
