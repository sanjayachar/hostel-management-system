package com.hostel.auth.record;

public record LoginRequest (
        String username,
        String password
){}
