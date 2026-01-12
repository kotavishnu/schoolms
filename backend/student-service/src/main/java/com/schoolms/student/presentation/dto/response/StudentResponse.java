package com.schoolms.student.presentation.dto.response;

import com.schoolms.student.domain.model.StudentStatus;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Student Response DTO (BE-019)
 */
public record StudentResponse(
    String id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    Integer age,
    String adhaarNumber,
    String address,
    String identificationMarks,
    String guardianName,
    String motherName,
    String phone,
    String email,
    StudentStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
