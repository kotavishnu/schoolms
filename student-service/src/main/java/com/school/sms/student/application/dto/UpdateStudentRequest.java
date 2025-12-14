package com.school.sms.student.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for updating student profile.
 * Only editable fields: firstName, lastName, mobile, status.
 */
@Data
public class UpdateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must contain 10-15 digits")
    private String mobile;

    @NotNull(message = "Version is required for optimistic locking")
    private Long version;
}
