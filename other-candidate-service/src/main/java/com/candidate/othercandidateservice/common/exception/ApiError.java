package com.candidate.othercandidateservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiError {
    private int status;
    private String message;
    private long timestamp;
    private String path;
}
