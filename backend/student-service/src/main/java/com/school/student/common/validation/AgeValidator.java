package com.school.student.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

/**
 * Validator implementation for @ValidAge annotation.
 * Validates that the date of birth results in an age between 3 and 18 years.
 */
public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true; // Let @NotNull handle null validation
        }

        LocalDate now = LocalDate.now();
        if (dateOfBirth.isAfter(now)) {
            return false; // Date in the future
        }

        int age = Period.between(dateOfBirth, now).getYears();
        return age >= min && age <= max;
    }
}
