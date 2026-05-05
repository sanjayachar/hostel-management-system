package com.hostel.student.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateDateRange {
    String message() default "To date must be after or equal to From date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
