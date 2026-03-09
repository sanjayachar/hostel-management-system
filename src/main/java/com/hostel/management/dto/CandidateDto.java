package com.hostel.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CandidateDto {
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String contactNumber;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String appliedPost;
}