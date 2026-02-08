package com.school.student.domain.model;

/**
 * Enrollment status enumeration.
 *
 * <p>Status Transitions:
 * <ul>
 *   <li>ACTIVE: Student is currently enrolled</li>
 *   <li>WITHDRAWN: Student withdrew before academic year completion</li>
 *   <li>COMPLETED: Academic year completed successfully</li>
 * </ul>
 *
 * <p>Valid Transitions:
 * <ul>
 *   <li>ACTIVE → WITHDRAWN (via withdraw())</li>
 *   <li>ACTIVE → COMPLETED (via complete())</li>
 * </ul>
 */
public enum EnrollmentStatus {
    /**
     * Student is currently enrolled for the academic year
     */
    ACTIVE,

    /**
     * Student withdrew from the enrollment before completion
     */
    WITHDRAWN,

    /**
     * Enrollment completed successfully at end of academic year
     */
    COMPLETED
}
