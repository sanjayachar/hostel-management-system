package com.hostel.chatservice.service;

import com.hostel.chatservice.dto.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserDisplayNameService {

    private static final String STAFF_NAME_QUERY = """
            SELECT NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS display_name
            FROM hostel.staff
            WHERE user_id = ?
            LIMIT 1
            """;
    private static final String STUDENT_NAME_QUERY = """
            SELECT NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS display_name
            FROM hostel.students
            WHERE user_id = ?
            LIMIT 1
            """;
    private static final String CANDIDATE_NAME_QUERY = """
            SELECT NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS display_name
            FROM hostel.candidates
            WHERE user_id = ?
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public String getDisplayName(ChatUser user) {
        return resolveDisplayName(user.userId(), user.username(), user.role());
    }

    public String resolveDisplayName(Long userId, String fallbackName, String role) {
        if (userId == null || role == null || role.isBlank()) {
            return normalizeFallback(fallbackName);
        }

        String query = switch (role) {
            case "ROLE_STAFF" -> STAFF_NAME_QUERY;
            case "ROLE_STUDENT" -> STUDENT_NAME_QUERY;
            case "ROLE_CANDIDATE" -> CANDIDATE_NAME_QUERY;
            default -> null;
        };

        if (query == null) {
            return normalizeFallback(fallbackName);
        }

        String displayName = jdbcTemplate.query(query, resultSet ->
                resultSet.next() ? resultSet.getString("display_name") : null, userId);

        if (displayName == null || displayName.isBlank()) {
            return normalizeFallback(fallbackName);
        }

        return displayName.trim();
    }

    private String normalizeFallback(String fallbackName) {
        if (fallbackName == null || fallbackName.isBlank()) {
            return "User";
        }

        if ("admin".equalsIgnoreCase(fallbackName)) {
            return "Admin";
        }

        return fallbackName.trim();
    }
}
