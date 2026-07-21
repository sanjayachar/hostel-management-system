package com.hostel.accommodation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelRoomDto {
    private Long roomId;
    @NotNull(message = "Hostel is required.")
    private Long hostelId;
    private String hostelName;
    @NotBlank(message = "Room number is required.")
    private String roomNumber;
    private Integer floorNumber;
    @NotBlank(message = "Room type is required.")
    private String roomType;
    @NotNull(message = "Room capacity is required.")
    @Min(value = 1, message = "Room capacity must be at least 1.")
    private Integer capacity;
    private Integer occupiedCount;
    private Integer availableBeds;
}
