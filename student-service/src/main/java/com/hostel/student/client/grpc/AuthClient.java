package com.hostel.student.client.grpc;

import com.hostel.proto.auth.AuthServiceGrpc;
import com.hostel.proto.auth.CreateUserRequest;
import com.hostel.proto.auth.CreateUserResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class AuthClient {

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub stub;

    public Long createUser(String userName, String password, String role) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUserName(userName)
                .setPassword(password)
                .setRole(role)
                .build();
        CreateUserResponse response = stub.createUser(request);
        return response.getUserId();
    }
}
