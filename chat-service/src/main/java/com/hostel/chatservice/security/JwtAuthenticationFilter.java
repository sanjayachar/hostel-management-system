package com.hostel.chatservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = createAuthentication(authHeader.substring(7));

            if (authenticationToken != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            log.warn("Invalid JWT received for chat-service: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    public UsernamePasswordAuthenticationToken createAuthentication(String jwt) {
        if (!jwtService.validateToken(jwt)) {
            return null;
        }

        Long userId = jwtService.extractUserId(jwt);
        Integer tokenVersion = jwtService.extractTokenVersion(jwt);
        String cachedVersion = stringRedisTemplate.opsForValue().get("tokenVersion:" + userId);

        if (cachedVersion == null || !cachedVersion.equals(String.valueOf(tokenVersion))) {
            return null;
        }

        String username = jwtService.extractUsername(jwt);
        String role = jwtService.extractRole(jwt);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UserPrincipal principal = new UserPrincipal(userId, username, role);

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }
}
