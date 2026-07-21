package com.hostel.accommodation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelDto {
    private Long hostelId;
    @NotBlank(message = "Hostel code is required.")
    private String hostelCode;
    @NotBlank(message = "Hostel name is required.")
    private String hostelName;
    @NotBlank(message = "Hostel type is required.")
    private String hostelType;
    private String address;
}
