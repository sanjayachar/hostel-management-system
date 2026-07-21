package com.hostel.accommodation.client.grpc;

import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.dto.RequesterProfileDto;
import com.hostel.proto.profile.ProfileServiceGrpc;
import com.hostel.proto.profile.UserProfileRequest;
import com.hostel.proto.profile.UserProfileResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

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

    public Optional<RequesterProfileDto> getProfile(Long userId, RoleEnum role) {
        if (userId == null || role == null) {
            return Optional.empty();
        }

        ProfileServiceGrpc.ProfileServiceBlockingStub stub = resolveStub(role);
        if (stub == null) {
            return Optional.empty();
        }

        UserProfileRequest request = UserProfileRequest.newBuilder()
                .setUserId(userId)
                .setRole(role.name())
                .build();

        try {
            UserProfileResponse response = stub.getProfile(request);
            if (!response.getFound()) {
                return Optional.empty();
            }

            return Optional.of(new RequesterProfileDto(
                    emptyToNull(response.getCode()),
                    emptyToNull(response.getDisplayName()),
                    emptyToNull(response.getEmail())
            ));
        } catch (StatusRuntimeException ex) {
            log.warn("Profile gRPC lookup failed for role {} and user {}", role, userId, ex);
            return Optional.empty();
        }
    }

    private ProfileServiceGrpc.ProfileServiceBlockingStub resolveStub(RoleEnum role) {
        return switch (role) {
            case ROLE_STUDENT -> studentProfileStub;
            case ROLE_STAFF -> staffProfileStub;
            case ROLE_CANDIDATE -> candidateProfileStub;
            default -> null;
        };
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
