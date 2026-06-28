package com.hostel.auth.controller;


import com.hostel.auth.dto.ChangePasswordRequest;
import com.hostel.auth.record.CreateUserRequest;
import com.hostel.auth.record.CreateUserResponse;
import com.hostel.auth.record.JwtAuthResponse;
import com.hostel.auth.record.LoginRequest;
import com.hostel.auth.entity.User;
import com.hostel.auth.record.CustomUserDetails;
import com.hostel.auth.security.JwtService;
import com.hostel.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;
    private final StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        User user = ((CustomUserDetails) authentication.getPrincipal()).user();
        stringRedisTemplate.opsForValue().set( "tokenVersion:" + user.getUserId(), String.valueOf(user.getTokenVersion()));
        String token = jwtService.generateToken(Objects.requireNonNull(user));
        return ResponseEntity.ok(new JwtAuthResponse(token, Boolean.TRUE.equals(user.getPasswordChangeRequired())));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        authService.logout();
        return ResponseEntity.ok("Logged out successfully..!");
    }

    @PostMapping("/internal/create-user")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(authService.createUser(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully.");
    }
}
