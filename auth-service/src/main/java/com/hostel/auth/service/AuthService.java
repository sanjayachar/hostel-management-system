package com.hostel.auth.service;

import com.hostel.auth.entity.Role;
import com.hostel.auth.entity.User;
import com.hostel.auth.enums.RoleEnum;
import com.hostel.auth.record.CreateUserRequest;
import com.hostel.auth.record.CreateUserResponse;
import com.hostel.auth.repository.RoleRepository;
import com.hostel.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
        stringRedisTemplate.opsForValue().set("tokenVersion:" + user.getUserId(), String.valueOf(user.getTokenVersion()));
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        Role role = roleRepository.findByRoleName(
                RoleEnum.valueOf(request.role())
        ).orElseThrow(() -> new RuntimeException("Role not found"));
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return new CreateUserResponse(savedUser.getUserId());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
