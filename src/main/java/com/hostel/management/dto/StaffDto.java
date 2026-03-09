package com.hostel.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StaffDto {
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String email;
    private String address;
    private String designation;
    private String department;
    private LocalDate dateOfJoining;
}