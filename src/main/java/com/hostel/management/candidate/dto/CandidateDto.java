package com.hostel.management.candidate.dto;

import com.hostel.management.common.validation.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CandidateDto {
    private Long candidateId;
    @NotBlank(message = "Candidate code is required.")
    private String candidateCode;
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    @NotBlank(message = "Gender is required.")
    private String gender;
    @NotNull(message = "Date of birth is required.")
    @MinimumAge(value = 20)
    private LocalDate dateOfBirth;
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid Email.")
    private String email;
    @NotBlank(message = "Contact number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    @NotBlank(message = "Address is required.")
    private String address;
    private String city;
    private String state;
    private String pinCode;
    @NotNull(message = "Applied post is required.")
    private String appliedPost;
}