package com.hostel.accommodation.service;

import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.dto.RequesterProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequesterProfileService {

    private static final String STUDENT_QUERY = """
            SELECT admission_number AS requester_code,
                   NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS requester_name
            FROM hostel.students
            WHERE user_id = ?
            LIMIT 1
            """;
    private static final String STAFF_QUERY = """
            SELECT employee_code AS requester_code,
                   NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS requester_name
            FROM hostel.staff
            WHERE user_id = ?
            LIMIT 1
            """;
    private static final String CANDIDATE_QUERY = """
            SELECT candidate_code AS requester_code,
                   NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), '') AS requester_name
            FROM hostel.candidates
            WHERE user_id = ?
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public RequesterProfileDto resolve(Long userId, RoleEnum role) {
        if (userId == null || role == null) {
            return new RequesterProfileDto(null, null);
        }

        String query = switch (role) {
            case ROLE_STUDENT -> STUDENT_QUERY;
            case ROLE_STAFF -> STAFF_QUERY;
            case ROLE_CANDIDATE -> CANDIDATE_QUERY;
            default -> null;
        };

        if (query == null) {
            return new RequesterProfileDto(null, "User #" + userId);
        }

        RequesterProfileDto profile = jdbcTemplate.query(query, resultSet -> {
            if (!resultSet.next()) {
                return null;
            }

            return new RequesterProfileDto(
                    resultSet.getString("requester_code"),
                    resultSet.getString("requester_name")
            );
        }, userId);

        if (profile == null) {
            return new RequesterProfileDto(null, "User #" + userId);
        }

        return profile;
    }
}
