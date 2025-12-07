package com.school.sms.student.domain.model;

/**
 * Enumeration representing the status of a student.
 *
 * <p>A student can be either ACTIVE or INACTIVE in the system.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public enum StudentStatus {
    /**
     * Student is currently active in the school.
     */
    ACTIVE,

    /**
     * Student is inactive (e.g., graduated, transferred, or withdrawn).
     */
    INACTIVE
}
