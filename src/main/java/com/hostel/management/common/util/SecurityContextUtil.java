package com.hostel.management.common.util;

import com.hostel.management.auth.entity.User;
import com.hostel.management.auth.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityContextUtil {

    private SecurityContextUtil() {
        // Prevent instantiation
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.user().getUserId() : null;
    }

    public static String getUsername() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    public static String getRole() {
        CustomUserDetails userDetails = getCurrentUser();

        if (userDetails == null) return null;

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }

    public static User getUser() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.user() : null;
    }
}
