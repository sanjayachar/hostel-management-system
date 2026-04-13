package com.hostel.management.auth.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private String status;
    private String message;
    private Map<String,String> errors;
}
