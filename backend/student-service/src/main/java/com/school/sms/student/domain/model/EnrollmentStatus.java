package com.school.sms.student.domain.model;

/**
 * Enumeration representing the status of a student's enrollment.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public enum EnrollmentStatus {
    /**
     * Student is currently enrolled for this academic year.
     */
    ENROLLED,

    /**
     * Student has completed this academic year.
     */
    COMPLETED,

    /**
     * Student has withdrawn during this academic year.
     */
    WITHDRAWN
}
