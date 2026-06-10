package com.hostel.auth.record;

public record CreateUserRequest(
        String username,
        String password,
        String role
) {
}
