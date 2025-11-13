package com.school.management.domain.student.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for student age
 * Validates that student age is between 3 and 18 years (Business Rule BR-1)
 *
 * @author School Management System
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAgeValidator.class)
@Documented
public @interface ValidAge {

    String message() default "Student age must be between 3 and 18 years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum age in years
     */
    int min() default 3;

    /**
     * Maximum age in years
     */
    int max() default 18;
}
