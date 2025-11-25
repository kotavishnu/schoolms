package com.school.sms.student.application.validator;

import com.school.sms.common.util.DateTimeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Validator implementation for @AgeRange annotation.
 */
public class AgeRangeValidator implements ConstraintValidator<AgeRange, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(AgeRange constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true; // Null check handled by @NotNull
        }

        int age = DateTimeUtils.calculateAge(dateOfBirth);
        return age >= minAge && age <= maxAge;
    }
}
