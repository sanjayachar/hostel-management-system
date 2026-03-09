package com.hostel.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentsDto {
    private Long studentId;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String personalEmail;
    private String fatherName;
    private String motherName;
    private String address;
    private Boolean hostelStatus;
}