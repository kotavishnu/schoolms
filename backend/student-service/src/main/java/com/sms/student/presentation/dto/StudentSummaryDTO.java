package com.sms.student.presentation.dto;

import com.sms.student.domain.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Simplified Student DTO for list/search responses.
 * Contains only essential information for display in tables.
 */
@Schema(description = "Student summary information for search results")
public record StudentSummaryDTO(
    @Schema(description = "Internal student ID", example = "1")
    Long studentId,

    @Schema(description = "Human-readable student key", example = "STU-2025-0001")
    String studentKey,

    @Schema(description = "Student's first name", example = "Rahul")
    String firstName,

    @Schema(description = "Student's last name", example = "Sharma")
    String lastName,

    @Schema(description = "Mobile number", example = "+919876543210")
    String mobile,

    @Schema(description = "Father or guardian name", example = "Raj Sharma")
    String fatherNameOrGuardian,

    @Schema(description = "Enrollment status", example = "Active")
    StudentStatus status,

    @Schema(description = "Record creation timestamp")
    LocalDateTime createdAt
) {}
