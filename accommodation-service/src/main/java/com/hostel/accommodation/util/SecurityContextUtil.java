package com.hostel.accommodation.util;

import com.hostel.accommodation.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {

    private SecurityContextUtil() {}

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    public static Long getUserId() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getRole() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getAuthorities().iterator().next().getAuthority() : null;
    }

    public static String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        return (String) authentication.getCredentials();
    }
}
