package com.hostel.accommodation.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumAgeValidator.class)
@Documented
public @interface MinimumAge {

    String message() default "Candidate must be at least 20 years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
