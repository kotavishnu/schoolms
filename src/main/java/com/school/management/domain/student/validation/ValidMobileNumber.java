package com.school.management.domain.student.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for mobile number format
 * Validates that mobile number is exactly 10 digits (Business Rule BR-2)
 *
 * @author School Management System
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMobileNumberValidator.class)
@Documented
public @interface ValidMobileNumber {

    String message() default "Mobile number must be exactly 10 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
