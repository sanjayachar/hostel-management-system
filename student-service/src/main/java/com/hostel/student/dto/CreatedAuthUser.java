package com.hostel.student.dto;

public record CreatedAuthUser(
        Long userId,
        String temporaryPassword
) {
}
