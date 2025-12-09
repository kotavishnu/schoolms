package com.sms.student.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student domain entity representing a student in the school.
 * This is a rich domain model containing business logic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private Long studentId;
    private String studentKey;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String address;
    private String fatherNameOrGuardian;
    private String motherName;
    private String identificationMark;
    private String adhaarNumber;
    private StudentStatus status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Activate the student enrollment.
     */
    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Deactivate the student enrollment.
     */
    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Check if student is currently active.
     */
    public boolean isActive() {
        return StudentStatus.ACTIVE.equals(this.status);
    }
}
