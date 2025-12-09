package com.sms.student.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating a new student.
 */
@Schema(description = "Request to create a new student")
public record CreateStudentRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Schema(description = "Student's first name", example = "Rahul", requiredMode = Schema.RequiredMode.REQUIRED)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Schema(description = "Student's last name", example = "Sharma", requiredMode = Schema.RequiredMode.REQUIRED)
    String lastName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth (age must be 3-18)", example = "2012-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate dateOfBirth,

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile number format")
    @Schema(description = "Mobile number (10-15 digits)", example = "+919876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    String mobile,

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address", example = "rahul.sharma@example.com")
    String email,

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    @Schema(description = "Residential address", example = "123 Main Street, City, State - 123456")
    String address,

    @Size(max = 100, message = "Guardian name must not exceed 100 characters")
    @Schema(description = "Father or guardian name", example = "Raj Sharma")
    String fatherNameOrGuardian,

    @Size(max = 100, message = "Mother name must not exceed 100 characters")
    @Schema(description = "Mother's name", example = "Priya Sharma")
    String motherName,

    @Size(max = 200, message = "Identification mark must not exceed 200 characters")
    @Schema(description = "Physical identification mark", example = "Mole on left cheek")
    String identificationMark,

    @Pattern(regexp = "^[0-9]{12}$", message = "Adhaar number must be 12 digits")
    @Schema(description = "12-digit Adhaar number", example = "123456789012")
    String adhaarNumber
) {}
