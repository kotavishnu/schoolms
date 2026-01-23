package com.school.student.domain.entity;

/**
 * Student Status Enumeration
 *
 * Defines the possible states of a student record.
 */
public enum StudentStatus {
    /**
     * Student is currently enrolled and active
     */
    ACTIVE,

    /**
     * Student is no longer active (graduated, withdrawn, or suspended)
     */
    INACTIVE
}
