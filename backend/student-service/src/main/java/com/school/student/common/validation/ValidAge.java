package com.school.student.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for student age.
 * Ensures age is between 3 and 18 years (BR-1).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
@Documented
public @interface ValidAge {
    String message() default "Student age must be between 3 and 18 years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 3;
    int max() default 18;
}
