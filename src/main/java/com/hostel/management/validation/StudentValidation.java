package com.hostel.management.validation;

import com.hostel.management.dto.StudentsDto;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class StudentValidation {
    public static Map<String, String> validateStudent(StudentsDto studentsDto){
        Map<String, String> errorMessage = new HashMap<>();
        validateDateOfBirth(studentsDto, errorMessage);
        return errorMessage;
    }

    private static void validateDateOfBirth(StudentsDto studentsDto, Map<String, String> errorMessage) {
        LocalDate dob = studentsDto.getDateOfBirth();
        if(dob != null){
            if(dob.isAfter(LocalDate.now())){
                errorMessage.put("dateOfBirth","Date of birth cannot be in the future");
                return;
            }
            int age = Period.between(dob, LocalDate.now()).getYears();
            if(age < 20){
                errorMessage.put("dateOfBirth","Student must be at least 20 years old");
            }
        }
    }
}
