package com.hostel.chatservice.client.grpc;

import com.hostel.chatservice.dto.ChatUserProfileDto;
import com.hostel.proto.profile.ProfileServiceGrpc;
import com.hostel.proto.profile.UserProfileListRequest;
import com.hostel.proto.profile.UserProfileRequest;
import com.hostel.proto.profile.UserProfileResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserProfileClient {

    @GrpcClient("student-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub studentProfileStub;

    @GrpcClient("staff-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub staffProfileStub;

    @GrpcClient("other-candidate-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub candidateProfileStub;

    public Optional<ChatUserProfileDto> getProfile(Long userId, String role) {
        if (userId == null || role == null || role.isBlank()) {
            return Optional.empty();
        }

        ProfileServiceGrpc.ProfileServiceBlockingStub stub = resolveStub(role);
        if (stub == null) {
            return Optional.empty();
        }

        UserProfileRequest request = UserProfileRequest.newBuilder()
                .setUserId(userId)
                .setRole(role)
                .build();

        try {
            UserProfileResponse response = stub.getProfile(request);
            if (!response.getFound()) {
                return Optional.empty();
            }

            return Optional.of(new ChatUserProfileDto(
                    response.getUserId(),
                    emptyToNull(response.getDisplayName()),
                    emptyToNull(response.getRole()),
                    emptyToNull(response.getEmail())
            ));
        } catch (StatusRuntimeException ex) {
            log.warn("Profile gRPC lookup failed for role {} and user {}: {}", role, userId, ex.getStatus());
            return Optional.empty();
        }
    }

    public Optional<ChatUserProfileDto> getProfile(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return getProfile(userId, "ROLE_STUDENT")
                .or(() -> getProfile(userId, "ROLE_STAFF"))
                .or(() -> getProfile(userId, "ROLE_CANDIDATE"));
    }

    public List<ChatUserProfileDto> listProfiles() {
        List<ChatUserProfileDto> profiles = new ArrayList<>();
        profiles.addAll(listProfiles("ROLE_STUDENT", studentProfileStub));
        profiles.addAll(listProfiles("ROLE_STAFF", staffProfileStub));
        profiles.addAll(listProfiles("ROLE_CANDIDATE", candidateProfileStub));

        return profiles.stream()
                .sorted(Comparator.comparing(profile -> safeLower(profile.displayName())))
                .toList();
    }

    private List<ChatUserProfileDto> listProfiles(
            String role,
            ProfileServiceGrpc.ProfileServiceBlockingStub stub
    ) {
        if (stub == null) {
            return List.of();
        }

        UserProfileListRequest request = UserProfileListRequest.newBuilder()
                .setRole(role)
                .build();

        try {
            return stub.listProfiles(request).getProfilesList().stream()
                    .filter(UserProfileResponse::getFound)
                    .map(this::toDto)
                    .toList();
        } catch (StatusRuntimeException ex) {
            log.warn("Profile gRPC list lookup failed for role {}: {}", role, ex.getStatus());
            return List.of();
        }
    }

    private ProfileServiceGrpc.ProfileServiceBlockingStub resolveStub(String role) {
        return switch (role) {
            case "ROLE_STUDENT" -> studentProfileStub;
            case "ROLE_STAFF" -> staffProfileStub;
            case "ROLE_CANDIDATE" -> candidateProfileStub;
            default -> null;
        };
    }

    private ChatUserProfileDto toDto(UserProfileResponse response) {
        return new ChatUserProfileDto(
                response.getUserId(),
                emptyToNull(response.getDisplayName()),
                emptyToNull(response.getRole()),
                emptyToNull(response.getEmail())
        );
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
