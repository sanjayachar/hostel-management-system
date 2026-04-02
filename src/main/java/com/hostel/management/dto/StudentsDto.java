package com.hostel.management.dto;

import com.hostel.management.validation.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentsDto {
    private Long studentId;
    @NotBlank(message = "Student Admisssion number is required.")
    private String admissionNumber;
    @NotBlank(message = "Student first name is required.")
    private String firstName;
    @NotBlank(message = "Student last name is required.")
    private String lastName;
    @NotBlank(message = "Student gender is required.")
    private String gender;
    @NotNull(message = "Student date of birth is required.")
    @MinimumAge(value = 20)
    private LocalDate dateOfBirth;
    @NotBlank(message = "Student contact number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Student personal email is required.")
    private String personalEmail;
    private String fatherName;
    private String motherName;
    @NotBlank(message = "Student address is required.")
    private String address;
    private Boolean hostelStatus;
}