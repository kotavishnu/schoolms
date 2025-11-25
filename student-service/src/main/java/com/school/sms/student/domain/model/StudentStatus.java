package com.school.sms.student.domain.model;

/**
 * Enum representing the status of a student.
 */
public enum StudentStatus {
    /**
     * Student is currently enrolled and active.
     */
    ACTIVE,

    /**
     * Student is inactive (soft delete or temporarily suspended).
     */
    INACTIVE,

    /**
     * Student has graduated from the school.
     */
    GRADUATED,

    /**
     * Student has transferred to another school.
     */
    TRANSFERRED
}
