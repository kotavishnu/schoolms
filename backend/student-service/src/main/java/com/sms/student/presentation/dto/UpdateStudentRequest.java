package com.sms.student.presentation.dto;

import com.sms.student.domain.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a student profile.
 * Only name, mobile, and status can be updated per business rules.
 */
@Schema(description = "Request to update student profile")
public record UpdateStudentRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Schema(description = "Student's first name", example = "Rahul", requiredMode = Schema.RequiredMode.REQUIRED)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Schema(description = "Student's last name", example = "Sharma", requiredMode = Schema.RequiredMode.REQUIRED)
    String lastName,

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile number format")
    @Schema(description = "Mobile number (10-15 digits)", example = "+919876543299", requiredMode = Schema.RequiredMode.REQUIRED)
    String mobile,

    @NotNull(message = "Status is required")
    @Schema(description = "Enrollment status", example = "Active", requiredMode = Schema.RequiredMode.REQUIRED)
    StudentStatus status
) {}
