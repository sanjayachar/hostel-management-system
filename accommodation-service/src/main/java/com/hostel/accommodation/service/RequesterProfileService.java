package com.hostel.accommodation.service;

import com.hostel.accommodation.client.grpc.UserProfileClient;
import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.dto.RequesterProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequesterProfileService {

    private final UserProfileClient userProfileClient;

    public RequesterProfileDto resolve(Long userId, RoleEnum role) {
        if (userId == null || role == null) {
            return new RequesterProfileDto(null, null, null);
        }

        return userProfileClient.getProfile(userId, role)
                .orElseGet(() -> unknownUser(userId));
    }

    private RequesterProfileDto unknownUser(Long userId) {
        return new RequesterProfileDto(null, "User #" + userId, null);
    }
}
