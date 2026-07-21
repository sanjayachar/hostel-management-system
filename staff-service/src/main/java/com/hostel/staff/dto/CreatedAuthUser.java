package com.hostel.staff.dto;

public record CreatedAuthUser(
        Long userId,
        String temporaryPassword
) {
}
