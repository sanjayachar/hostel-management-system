package com.hostel.staff.grpc;

import com.hostel.proto.profile.ProfileServiceGrpc;
import com.hostel.proto.profile.UserProfileListRequest;
import com.hostel.proto.profile.UserProfileListResponse;
import com.hostel.proto.profile.UserProfileRequest;
import com.hostel.proto.profile.UserProfileResponse;
import com.hostel.staff.entity.Staff;
import com.hostel.staff.service.StaffService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class StaffProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {

    private static final String ROLE_STAFF = "ROLE_STAFF";

    private final StaffService staffService;

    @Override
    public void getProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        try {
            UserProfileResponse response = staffService.getActiveStaffByUserId(request.getUserId())
                    .map(this::toResponse)
                    .orElseGet(() -> UserProfileResponse.newBuilder()
                            .setFound(false)
                            .setUserId(request.getUserId())
                            .setRole(ROLE_STAFF)
                            .build());

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Staff profile lookup failed for user {}", request.getUserId(), ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to resolve staff profile")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    @Override
    public void listProfiles(UserProfileListRequest request, StreamObserver<UserProfileListResponse> responseObserver) {
        try {
            UserProfileListResponse.Builder response = UserProfileListResponse.newBuilder();
            staffService.getActiveStaffs().stream()
                    .map(this::toResponse)
                    .forEach(response::addProfiles);

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Staff profile list lookup failed", ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to list staff profiles")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    private UserProfileResponse toResponse(Staff staff) {
        return UserProfileResponse.newBuilder()
                .setFound(true)
                .setUserId(defaultLong(staff.getUserId()))
                .setRole(ROLE_STAFF)
                .setCode(defaultString(staff.getEmployeeCode()))
                .setDisplayName(fullName(staff.getFirstName(), staff.getLastName()))
                .setEmail(defaultString(staff.getEmail()))
                .build();
    }

    private String fullName(String firstName, String lastName) {
        String name = String.join(" ",
                defaultString(firstName).trim(),
                defaultString(lastName).trim()
        ).trim();

        return name.isBlank() ? "Staff" : name;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
