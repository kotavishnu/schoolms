package com.school.management.domain.student.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for @ValidMobileNumber annotation
 * Validates mobile number is exactly 10 digits
 *
 * @author School Management System
 * @version 1.0
 */
public class ValidMobileNumberValidator implements ConstraintValidator<ValidMobileNumber, String> {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10}$");

    @Override
    public void initialize(ValidMobileNumber constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext context) {
        if (mobile == null || mobile.isEmpty()) {
            return true; // Use @NotNull/@NotBlank for null/empty validation
        }

        return MOBILE_PATTERN.matcher(mobile).matches();
    }
}
