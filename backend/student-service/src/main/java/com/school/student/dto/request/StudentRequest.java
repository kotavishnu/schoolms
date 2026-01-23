package com.school.student.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Student Registration Request DTO
 *
 * Used for creating new students.
 * Contains all required fields for student registration.
 *
 * Validation rules mirror the business rules in Drools,
 * providing fail-fast validation at the API boundary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String address;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "Guardian name is required")
    @Size(min = 1, max = 100, message = "Guardian name must be between 1 and 100 characters")
    private String guardianName;

    @NotBlank(message = "Mother name is required")
    @Size(min = 1, max = 100, message = "Mother name must be between 1 and 100 characters")
    private String motherName;

    @Size(max = 200, message = "Identification marks must not exceed 200 characters")
    private String identificationMarks;
}
