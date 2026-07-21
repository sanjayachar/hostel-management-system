package com.hostel.staff.dto;

import com.hostel.staff.common.enums.RoleEnum;
import com.hostel.staff.common.validation.ValidateDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@ValidateDateRange
public class AccommodationRequestDto {
    private Long requestId;
    @NotBlank(message = "Request type is required.")
    private String requestType;
    @NotBlank(message = "Request reason is required.")
    private String reason;
    @NotNull(message = "From date is required.")
    private LocalDate fromDate;
    @NotNull(message = "To date is required.")
    private LocalDate toDate;
    @NotNull(message = "Number of days is required.")
    private Integer noOfDays;
    @NotNull(message = "Number of persons is required.")
    private Integer noOfPersons;
    private String status;
    private String decisionNote;
    private Long userId;
    private RoleEnum userRole;
    private String requesterCode;
    private String requesterName;
    private Long allocationId;
    private Long hostelId;
    private String hostelName;
    private Long roomId;
    private String roomNumber;
    private String bedNumber;
    private String allocationStatus;
}
