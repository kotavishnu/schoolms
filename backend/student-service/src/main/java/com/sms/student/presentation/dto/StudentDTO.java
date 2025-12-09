package com.sms.student.presentation.dto;

import com.sms.student.domain.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student Data Transfer Object for API responses.
 * Uses Java 21 record type for immutability.
 */
@Schema(description = "Student information")
public record StudentDTO(
    @Schema(description = "Internal student ID", example = "1")
    Long studentId,

    @Schema(description = "Human-readable student key", example = "STU-2025-0001")
    String studentKey,

    @Schema(description = "Student's first name", example = "Rahul")
    String firstName,

    @Schema(description = "Student's last name", example = "Sharma")
    String lastName,

    @Schema(description = "Date of birth", example = "2012-05-15")
    LocalDate dateOfBirth,

    @Schema(description = "Calculated age in years", example = "13")
    Integer age,

    @Schema(description = "Mobile number", example = "+919876543210")
    String mobile,

    @Schema(description = "Email address", example = "rahul.sharma@example.com")
    String email,

    @Schema(description = "Residential address")
    String address,

    @Schema(description = "Father or guardian name", example = "Raj Sharma")
    String fatherNameOrGuardian,

    @Schema(description = "Mother's name", example = "Priya Sharma")
    String motherName,

    @Schema(description = "Physical identification mark")
    String identificationMark,

    @Schema(description = "12-digit Adhaar number", example = "123456789012")
    String adhaarNumber,

    @Schema(description = "Enrollment status", example = "Active")
    StudentStatus status,

    @Schema(description = "Record creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {}
