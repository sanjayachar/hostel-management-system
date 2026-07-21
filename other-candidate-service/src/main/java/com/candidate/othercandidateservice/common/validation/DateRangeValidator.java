package com.candidate.othercandidateservice.common.validation;


import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidateDateRange, AccommodationRequestDto> {

    @Override
    public boolean isValid(AccommodationRequestDto dto, ConstraintValidatorContext context) {
        System.out.println("FROM: " + dto.getFromDate());
        System.out.println("TO: " + dto.getToDate());
        if (dto.getFromDate() == null || dto.getToDate() == null) {
            return true;
        }
        if (dto.getToDate().isBefore(dto.getFromDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("To date must be after From date")
                    .addPropertyNode("toDate")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}
