package com.hostel.management.response;

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
