package com.hostel.accommodation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDecisionDto {
    @NotNull(message = "Request id is required.")
    private Long requestId;
    @NotBlank(message = "Decision status is required.")
    private String status;
    private Long roomId;
    private String bedNumber;
    private String decisionNote;
}
