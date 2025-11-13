package com.school.management.domain.student.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

/**
 * Validator for @ValidAge annotation
 * Validates student age is between configured minimum and maximum
 *
 * @author School Management System
 * @version 1.0
 */
public class ValidAgeValidator implements ConstraintValidator<ValidAge, Object> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull for null validation
        }

        LocalDate dateOfBirth = extractDateOfBirth(value);
        if (dateOfBirth == null) {
            return true; // Cannot validate without date of birth
        }

        // Calculate the dates that represent the exact age boundaries
        LocalDate today = LocalDate.now();
        LocalDate oldestAllowedDate = today.minusYears(maxAge); // Person born on this date is exactly maxAge
        LocalDate youngestAllowedDate = today.minusYears(minAge); // Person born on this date is exactly minAge

        // Person must be born on or after oldestAllowedDate (not older than maxAge)
        // Person must be born on or before youngestAllowedDate (not younger than minAge)
        return !dateOfBirth.isBefore(oldestAllowedDate) && !dateOfBirth.isAfter(youngestAllowedDate);
    }

    /**
     * Extracts date of birth from the object being validated
     * Supports direct LocalDate or objects with getDateOfBirth method
     */
    private LocalDate extractDateOfBirth(Object value) {
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }

        try {
            // Try to call getDateOfBirth() method using reflection
            var method = value.getClass().getMethod("getDateOfBirth");
            Object result = method.invoke(value);
            return (LocalDate) result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calculates age from date of birth
     */
    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
