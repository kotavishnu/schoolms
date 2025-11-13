package com.school.management.domain.student;

/**
 * Enumeration of possible student statuses
 *
 * @author School Management System
 * @version 1.0
 */
public enum StudentStatus {
    /**
     * Student is currently active and enrolled
     */
    ACTIVE,

    /**
     * Student is temporarily inactive (e.g., on leave)
     */
    INACTIVE,

    /**
     * Student has graduated from the school
     */
    GRADUATED,

    /**
     * Student has transferred to another school
     */
    TRANSFERRED,

    /**
     * Student has withdrawn from the school
     */
    WITHDRAWN
}
