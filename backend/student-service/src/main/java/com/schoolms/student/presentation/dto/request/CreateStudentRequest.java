package com.schoolms.student.presentation.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Create Student Request DTO (BE-018)
 */
public record CreateStudentRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @NotBlank(message = "Adhaar number is required")
    @Pattern(regexp = "^\\d{12}$", message = "Adhaar number must be exactly 12 digits")
    String adhaarNumber,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be exactly 10 digits")
    String phone,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    String address,

    @NotBlank(message = "Guardian name is required")
    @Size(max = 100, message = "Guardian name must not exceed 100 characters")
    String guardianName,

    @NotBlank(message = "Mother name is required")
    @Size(max = 100, message = "Mother name must not exceed 100 characters")
    String motherName,

    @Size(max = 200, message = "Identification marks must not exceed 200 characters")
    String identificationMarks
) {
}
