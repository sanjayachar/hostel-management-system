package com.hostel.auth.grpc;

import com.hostel.auth.service.AuthService;
import com.hostel.proto.auth.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase{
    private final AuthService authService;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseStreamObserver) {
        com.hostel.auth.record.CreateUserRequest createUserRequest = new com.hostel.auth.record.CreateUserRequest(request.getUserName(), request.getPassword(), request.getRole());
        com.hostel.auth.record.CreateUserResponse createUserResponse = authService.createUser(createUserRequest);
        CreateUserResponse response = CreateUserResponse.newBuilder()
                .setUserId(createUserResponse.userId()).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        authService.deleteUser(request.getUserId());
        DeleteUserResponse response = DeleteUserResponse.newBuilder().setMessage("User deleted").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
