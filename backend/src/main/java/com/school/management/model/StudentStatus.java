package com.school.management.model;

/**
 * Enum representing the status of a student in the school system.
 *
 * Status transitions:
 * - ACTIVE → INACTIVE (temporary leave)
 * - ACTIVE → GRADUATED (completed Class 10)
 * - ACTIVE → TRANSFERRED (moved to different school)
 * - INACTIVE → ACTIVE (returned from leave)
 */
public enum StudentStatus {
    /**
     * Student is currently enrolled and attending classes
     */
    ACTIVE,

    /**
     * Student is temporarily not attending (leave of absence)
     */
    INACTIVE,

    /**
     * Student has completed Class 10 and graduated
     */
    GRADUATED,

    /**
     * Student has transferred to another school
     */
    TRANSFERRED
}
