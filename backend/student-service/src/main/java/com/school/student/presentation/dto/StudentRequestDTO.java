package com.school.student.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.student.common.validation.ValidAge;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Request DTO for student registration and updates.
 * Immutable with Bean Validation annotations.
 *
 * <p>Field naming matches OpenAPI specification (camelCase).
 * Validation rules match business rules and database constraints.
 */
@Builder
public record StudentRequestDTO(

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    String lastName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @ValidAge(message = "Student age must be between 3 and 18 years")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth,

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    String mobile,

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    String address,

    @Size(max = 100, message = "Father's name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Father's name must contain only letters and spaces")
    String fathersName,

    @Size(max = 100, message = "Mother's name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Mother's name must contain only letters and spaces")
    String mothersName,

    @Size(max = 200, message = "Identification mark must not exceed 200 characters")
    String identificationMark,

    @Pattern(regexp = "^\\d{12}$|^$", message = "Aadhaar number must be exactly 12 digits")
    String aadhaarNumber
) {
    /**
     * Custom validation: At least one guardian name is required (BR-5).
     */
    public StudentRequestDTO {
        if ((fathersName == null || fathersName.isBlank()) &&
            (mothersName == null || mothersName.isBlank())) {
            throw new IllegalArgumentException("At least one guardian name (father's or mother's) is required");
        }
    }
}
