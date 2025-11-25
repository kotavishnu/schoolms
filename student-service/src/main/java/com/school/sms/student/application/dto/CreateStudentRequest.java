package com.school.sms.student.application.dto;

import com.school.sms.student.application.validator.AgeRange;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for creating a new student.
 * Includes validation annotations.
 */
@Data
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @AgeRange(min = 3, max = 18, message = "Student age must be between 3 and 18 years")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must contain 10-15 digits")
    private String mobile;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "Father/Guardian name is required")
    @Size(min = 2, max = 100, message = "Father/Guardian name must be between 2 and 100 characters")
    private String fatherName;

    @Size(max = 100, message = "Mother name must not exceed 100 characters")
    private String motherName;

    @Size(max = 100, message = "Identification mark must not exceed 100 characters")
    private String identificationMark;

    @Pattern(regexp = "^[0-9]{12}$|^$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
}
