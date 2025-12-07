package com.school.sms.student.domain.model;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Value object representing audit information for entities.
 *
 * <p>Tracks creation and modification timestamps and users.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Value
public class AuditInfo {

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;

    /**
     * Creates an AuditInfo instance.
     *
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     * @param createdBy the user who created the record
     * @param updatedBy the user who last updated the record
     */
    public AuditInfo(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
    ) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    /**
     * Creates a new AuditInfo for initial creation.
     *
     * @param createdBy the user creating the record
     * @return a new AuditInfo instance
     */
    public static AuditInfo forCreation(String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(now, now, createdBy, createdBy);
    }

    /**
     * Creates an updated AuditInfo instance.
     *
     * @param updatedBy the user updating the record
     * @return a new AuditInfo instance with updated timestamp
     */
    public AuditInfo withUpdate(String updatedBy) {
        return new AuditInfo(this.createdAt, LocalDateTime.now(), this.createdBy, updatedBy);
    }
}
