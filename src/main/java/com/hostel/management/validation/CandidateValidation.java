package com.hostel.management.validation;

import com.hostel.management.dto.CandidateDto;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class CandidateValidation {
    public static Map<String, String> validateCandidate(@Valid CandidateDto candidateDto) {
        Map<String, String> errorMessage = new HashMap<>();
        validateDateOfBirth(candidateDto, errorMessage);
        return errorMessage;
    }

    private static void validateDateOfBirth(@Valid CandidateDto candidateDto, Map<String, String> errorMessage) {
        LocalDate dob = candidateDto.getDateOfBirth();
        if(dob != null){
            if(dob.isAfter(LocalDate.now())){
                errorMessage.put("dateOfBirth","Date of birth cannot be in the future");
                return;
            }
            int age = Period.between(dob, LocalDate.now()).getYears();
            if(age < 20){
                errorMessage.put("dateOfBirth","Candidate must be at least 20 years old");
            }
        }
    }
}
