package com.school.student.domain.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Student Validation Request
 *
 * Fact object passed to Drools rules engine for validation.
 * Contains all student data that needs to be validated
 * according to business rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentValidationRequest {

    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String aadhaarNumber;
    private String guardianName;
    private String motherName;
    private String address;
    private String identificationMarks;

    /**
     * Flag to indicate if mobile uniqueness should be checked
     * Set to false during update operations where mobile hasn't changed
     */
    private boolean checkMobileUniqueness = true;

    /**
     * Flag to indicate if email uniqueness should be checked
     */
    private boolean checkEmailUniqueness = true;

    /**
     * Calculate age from date of birth
     * Used for BR-STU-001 validation
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
