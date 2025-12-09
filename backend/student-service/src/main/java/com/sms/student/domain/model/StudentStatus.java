package com.sms.student.domain.model;

/**
 * Enum representing the status of a student's enrollment.
 */
public enum StudentStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
