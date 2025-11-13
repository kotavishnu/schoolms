package com.school.management.domain.academic;

import com.school.management.domain.base.BaseEntity;
import com.school.management.domain.student.Enrollment;
import com.school.management.shared.exception.BusinessException;
import com.school.management.shared.exception.ValidationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * SchoolClass domain entity
 * Represents a class section (e.g., Class 5-A)
 *
 * Business Rules:
 * - Capacity must be between 1 and 100
 * - Current enrollment cannot exceed max capacity
 * - Class-section-year combination must be unique
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "classes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_class_section_year",
            columnNames = {"class_name", "section", "academic_year_id"}
        )
    },
    indexes = {
        @Index(name = "idx_classes_academic_year", columnList = "academic_year_id"),
        @Index(name = "idx_classes_name_section", columnList = "class_name,section"),
        @Index(name = "idx_classes_is_active", columnList = "is_active")
    })
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass extends BaseEntity {

    @Column(nullable = false, length = 10, name = "class_name")
    @Enumerated(EnumType.STRING)
    private ClassName className;

    @Column(nullable = false, length = 1)
    @Pattern(regexp = "^[A-Z]$", message = "Section must be a single uppercase letter A-Z")
    private String section;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false, name = "max_capacity")
    @Min(value = 1, message = "Max capacity must be at least 1")
    @Max(value = 100, message = "Max capacity cannot exceed 100")
    private Integer maxCapacity;

    @Column(nullable = false, name = "current_enrollment")
    @Min(value = 0, message = "Current enrollment cannot be negative")
    private Integer currentEnrollment = 0;

    @Column(nullable = false, columnDefinition = "boolean default true", name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "enrolledClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    /**
     * Validate enrollment constraints before persistence
     */
    @PrePersist
    @PreUpdate
    private void validateEnrollment() {
        if (currentEnrollment < 0) {
            throw new ValidationException("Current enrollment cannot be negative");
        }
        if (currentEnrollment > maxCapacity) {
            throw new ValidationException(
                "Current enrollment cannot exceed max capacity");
        }
    }

    /**
     * Check if class has available capacity for new enrollment
     * @return true if capacity available, false otherwise
     */
    public boolean hasAvailableCapacity() {
        return currentEnrollment < maxCapacity;
    }

    /**
     * Get number of available seats
     * @return number of available seats
     */
    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment;
    }

    /**
     * Get capacity utilization percentage
     * @return utilization percentage (0-100)
     */
    public double getCapacityUtilization() {
        if (maxCapacity == 0) {
            return 0.0;
        }
        return (currentEnrollment * 100.0) / maxCapacity;
    }

    /**
     * Increment enrollment count
     * Throws exception if at full capacity
     */
    public void incrementEnrollment() {
        if (!hasAvailableCapacity()) {
            throw new BusinessException(
                String.format("Class %s-%s is at full capacity (%d/%d)",
                    className.getDisplayName(), section, currentEnrollment, maxCapacity));
        }
        this.currentEnrollment++;
    }

    /**
     * Decrement enrollment count
     * Does nothing if enrollment is already 0
     */
    public void decrementEnrollment() {
        if (currentEnrollment > 0) {
            this.currentEnrollment--;
        }
    }

    /**
     * Get full class name (e.g., "Class 5-A")
     * @return formatted class name
     */
    public String getFullClassName() {
        return String.format("Class %s-%s", className.getDisplayName(), section);
    }

    /**
     * Check if class is full
     * @return true if at capacity, false otherwise
     */
    public boolean isFull() {
        return currentEnrollment >= maxCapacity;
    }
}
