package com.hostel.auth.record;

public record JwtAuthResponse ( String token, Boolean passwordChangeRequired ) { }
