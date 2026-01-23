package com.school.student.domain.entity;

/**
 * Enrollment Status Enumeration
 *
 * Defines the possible states of a student enrollment record.
 */
public enum EnrollmentStatus {
    /**
     * Currently enrolled for the academic year
     */
    ACTIVE,

    /**
     * Withdrawn from the grade/class
     */
    WITHDRAWN,

    /**
     * Successfully completed the grade/class
     */
    COMPLETED
}
