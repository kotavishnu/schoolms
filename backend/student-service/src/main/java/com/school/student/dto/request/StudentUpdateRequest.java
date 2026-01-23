package com.school.student.dto.request;

import com.school.student.domain.entity.StudentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Student Update Request DTO
 *
 * Used for updating existing students.
 * Contains ONLY the fields that are allowed to be modified.
 *
 * BR-STU-007: Editable Fields Constraint
 * Only firstName, lastName, mobile, and status can be updated.
 * All other fields (dateOfBirth, email, aadhaarNumber, etc.) are immutable.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    @NotNull(message = "Status is required")
    private StudentStatus status;

    /**
     * Version for optimistic locking (D-003)
     * MUST be provided by client to prevent concurrent modification
     */
    @NotNull(message = "Version is required for optimistic locking")
    private Integer version;
}
