package com.hostel.student.grpc;

import com.hostel.proto.profile.ProfileServiceGrpc;
import com.hostel.proto.profile.UserProfileListRequest;
import com.hostel.proto.profile.UserProfileListResponse;
import com.hostel.proto.profile.UserProfileRequest;
import com.hostel.proto.profile.UserProfileResponse;
import com.hostel.student.entity.Students;
import com.hostel.student.service.StudentsService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class StudentProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {

    private static final String ROLE_STUDENT = "ROLE_STUDENT";

    private final StudentsService studentsService;

    @Override
    public void getProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        try {
            UserProfileResponse response = studentsService.getActiveStudentByUserId(request.getUserId())
                    .map(this::toResponse)
                    .orElseGet(() -> UserProfileResponse.newBuilder()
                            .setFound(false)
                            .setUserId(request.getUserId())
                            .setRole(ROLE_STUDENT)
                            .build());

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Student profile lookup failed for user {}", request.getUserId(), ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to resolve student profile")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    @Override
    public void listProfiles(UserProfileListRequest request, StreamObserver<UserProfileListResponse> responseObserver) {
        try {
            UserProfileListResponse.Builder response = UserProfileListResponse.newBuilder();
            studentsService.getActiveStudents().stream()
                    .map(this::toResponse)
                    .forEach(response::addProfiles);

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Student profile list lookup failed", ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to list student profiles")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    private UserProfileResponse toResponse(Students student) {
        return UserProfileResponse.newBuilder()
                .setFound(true)
                .setUserId(defaultLong(student.getUserId()))
                .setRole(ROLE_STUDENT)
                .setCode(defaultString(student.getAdmissionNumber()))
                .setDisplayName(fullName(student.getFirstName(), student.getLastName()))
                .setEmail(defaultString(student.getPersonalEmail()))
                .build();
    }

    private String fullName(String firstName, String lastName) {
        String name = String.join(" ",
                defaultString(firstName).trim(),
                defaultString(lastName).trim()
        ).trim();

        return name.isBlank() ? "Student" : name;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
