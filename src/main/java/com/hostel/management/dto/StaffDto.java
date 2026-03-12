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
public class StaffDto {
    private Long staffId;
    @NotBlank(message = "Employee code is required.")
    private String employeeCode;
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    @NotBlank(message = "Gender is required.")
    private String gender;
    @NotNull(message = "Date of birth is required.")
    @MinimumAge(value = 25, message = "Candidate must be at least 25 years old")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Contact number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid Email.")
    private String email;
    @NotBlank(message = "Address is required.")
    private String address;
    @NotBlank(message = "Designation is required.")
    private String designation;
    @NotBlank(message = "Department is required.")
    private String department;
    @NotNull(message = "Date of joining is required.")
    private LocalDate dateOfJoining;
}