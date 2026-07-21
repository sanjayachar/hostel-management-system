package com.hostel.staff.validation;

import com.hostel.staff.dto.StaffDto;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class StaffValidation {
    public static Map<String, String> validateStaff(@Valid StaffDto staffDto) {
        Map<String, String> errorMessage = new HashMap<>();
        validateDateOfBirth(staffDto, errorMessage);
        return errorMessage;
    }

    private static void validateDateOfBirth(@Valid StaffDto staffDto, Map<String, String> errorMessage) {
        LocalDate dob = staffDto.getDateOfBirth();
        if(dob != null){
            if(dob.isAfter(LocalDate.now())){
                errorMessage.put("dateOfBirth","Date of birth cannot be in the future");
                return;
            }
            int age = Period.between(dob, LocalDate.now()).getYears();
            if(age < 25){
                errorMessage.put("dateOfBirth","Staff must be at least 25 years old");
            }
        }
    }
}
