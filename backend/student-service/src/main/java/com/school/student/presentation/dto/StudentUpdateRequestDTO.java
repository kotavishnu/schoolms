package com.school.student.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * Request DTO for updating student profile.
 * Only allows updating specific fields (not all fields are mutable).
 *
 * <p>Immutable with Bean Validation.
 * Includes version for optimistic locking.
 */
@Builder
public record StudentUpdateRequestDTO(

    @NotNull(message = "Version is required for optimistic locking")
    Long version,

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    String firstName,

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    String lastName,

    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    String mobile,

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    String address,

    @Size(max = 200, message = "Identification mark must not exceed 200 characters")
    String identificationMark,

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    String status
) {
}
