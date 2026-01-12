package com.schoolms.student.presentation.dto.request;

import com.schoolms.student.domain.model.StudentStatus;
import jakarta.validation.constraints.*;

/**
 * Update Student Request DTO (BE-018)
 * All fields are optional for partial updates
 */
public record UpdateStudentRequest(
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be exactly 10 digits")
    String phone,

    StudentStatus status
) {
}
