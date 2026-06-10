package com.hostel.accommodation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        if (jwtService.validateToken(jwt)) {
            Long userId = jwtService.extractUserId(jwt);
            Integer tokenVersion = jwtService.extractTokenVersion(jwt);
            String userName = jwtService.extractUsername(jwt);
            Integer cachedVersion = Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("tokenVersion:" + userId)));
            if (!cachedVersion.equals(tokenVersion)) {
                filterChain.doFilter(request, response);
                return;
            }
            String role = jwtService.extractRole(jwt);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
            System.out.println("Role from token: " + role);
            UserPrincipal userPrincipal = new UserPrincipal(userId, userName, role);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
