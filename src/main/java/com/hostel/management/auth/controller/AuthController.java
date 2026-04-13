package com.hostel.management.auth.controller;

import com.hostel.management.auth.dto.JwtAuthResponse;
import com.hostel.management.auth.dto.LoginRequest;
import com.hostel.management.auth.entity.User;
import com.hostel.management.auth.security.CustomUserDetails;
import com.hostel.management.auth.security.JwtService;
import com.hostel.management.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        User user = ((CustomUserDetails) authentication.getPrincipal()).user();
        String token = jwtService.generateToken(Objects.requireNonNull(user));
        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        authService.logout();
        return ResponseEntity.ok("Logged out successfully..!");
    }
}
