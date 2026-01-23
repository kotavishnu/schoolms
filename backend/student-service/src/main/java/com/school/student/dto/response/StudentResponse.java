package com.school.student.dto.response;

import com.school.student.domain.entity.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student Response DTO
 *
 * Returned from all Student API endpoints.
 * Contains complete student information.
 *
 * IMPORTANT: Uses 'studentId' as the business key (NOT 'id').
 * The internal database ID is never exposed to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    /**
     * Business key (STD-YYYYMMDD-NNNN)
     * This is the identifier clients should use
     */
    private String studentId;

    // Personal Information
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String aadhaarNumber;
    private String identificationMarks;

    // Guardian Information
    private String guardianName;
    private String motherName;

    // Contact Information
    private String mobile;
    private String email;
    private String address;

    // Status
    private StudentStatus status;

    /**
     * Version for optimistic locking
     * Client must send this back during updates
     */
    private Integer version;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Calculated field: age in years
     * Derived from dateOfBirth
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
