package com.candidate.othercandidateservice.record;


public record CreateUserRequest(
        String username,
        String password,
        String role
) {
}
