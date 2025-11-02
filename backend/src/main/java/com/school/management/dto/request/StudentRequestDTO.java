package com.school.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for student registration and update requests.
 *
 * Contains validation rules for all student input fields.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;

    @Size(max = 50, message = "Caste must not exceed 50 characters")
    private String caste;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Mobile number must be 10 digits starting with 6, 7, 8, or 9")
    private String mobile;

    @Size(max = 50, message = "Religion must not exceed 50 characters")
    private String religion;

    @Size(max = 200, message = "Identification marks must not exceed 200 characters")
    private String molesOnBody;

    @NotBlank(message = "Mother's name is required")
    @Size(max = 100, message = "Mother's name must not exceed 100 characters")
    private String motherName;

    @NotBlank(message = "Father's name is required")
    @Size(max = 100, message = "Father's name must not exceed 100 characters")
    private String fatherName;

    @NotNull(message = "Class ID is required")
    @Positive(message = "Class ID must be positive")
    private Long classId;
}
