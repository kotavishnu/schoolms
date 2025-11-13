package com.school.management.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base entity class for all domain entities.
 *
 * <p>Provides common fields for all entities:</p>
 * <ul>
 *   <li>id: Primary key (auto-generated)</li>
 *   <li>createdAt: Timestamp when entity was created</li>
 *   <li>createdBy: User ID who created the entity</li>
 *   <li>updatedAt: Timestamp when entity was last updated</li>
 *   <li>updatedBy: User ID who last updated the entity</li>
 * </ul>
 *
 * <p>Implements proper equals() and hashCode() based on entity ID.</p>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "students")
 * public class Student extends BaseEntity {
 *     private String studentCode;
 *     private String firstName;
 *     // ... other fields
 * }
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key - auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Timestamp when entity was created.
     * Automatically set by JPA on persist.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * User ID who created the entity.
     * Set by application via AuditorAware.
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    /**
     * Timestamp when entity was last updated.
     * Automatically updated by JPA on every update.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User ID who last updated the entity.
     * Set by application via AuditorAware.
     */
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /**
     * Sets created and updated timestamps before persist.
     * Called automatically by JPA.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Updates the updated timestamp before update.
     * Called automatically by JPA.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks equality based on entity ID.
     *
     * <p>Two entities are equal if:</p>
     * <ul>
     *   <li>Both have non-null IDs</li>
     *   <li>IDs are equal</li>
     *   <li>Both are instances of the same class</li>
     * </ul>
     *
     * <p>Transient entities (without ID) are never equal.</p>
     *
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;

        // Transient entities are never equal
        if (this.id == null || that.id == null) {
            return false;
        }

        return Objects.equals(this.id, that.id);
    }

    /**
     * Generates hash code based on entity ID.
     *
     * <p>Returns consistent hash code for persisted entities.
     * For transient entities, uses identity hash code.</p>
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode();
    }

    /**
     * Returns string representation of entity.
     *
     * @return string with class name and ID
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Checks if entity is persisted (has ID).
     *
     * @return true if entity has ID, false otherwise
     */
    public boolean isPersisted() {
        return this.id != null;
    }

    /**
     * Checks if entity is transient (no ID).
     *
     * @return true if entity has no ID, false otherwise
     */
    public boolean isTransient() {
        return this.id == null;
    }
}
