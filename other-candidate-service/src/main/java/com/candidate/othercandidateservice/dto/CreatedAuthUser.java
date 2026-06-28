package com.candidate.othercandidateservice.dto;

public record CreatedAuthUser(
        Long userId,
        String temporaryPassword
) {
}
