package com.hostel.management.controller;

import com.hostel.management.dto.JwtAuthResponse;
import com.hostel.management.dto.LoginRequest;
import com.hostel.management.modal.User;
import com.hostel.management.security.CustomUserDetails;
import com.hostel.management.security.JwtService;
import com.hostel.management.service.AuthService;
import com.hostel.management.util.SecurityContextUtil;
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
