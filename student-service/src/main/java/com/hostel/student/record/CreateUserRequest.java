package com.hostel.student.record;


public record CreateUserRequest(
        String username,
        String password,
        String role
) {
}
