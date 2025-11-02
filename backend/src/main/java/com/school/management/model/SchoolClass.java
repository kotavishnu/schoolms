package com.school.management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a school class (Classes 1-10) with section and capacity management.
 *
 * Database Table: school_classes
 *
 * Business Rules:
 * - Class numbers must be between 1 and 10
 * - Default capacity is 50 students per section
 * - Academic year format: "2024-2025"
 * - Unique constraint on (class_number, section, academic_year)
 *
 * @author School Management Team
 */
@Entity
@Table(name = "school_classes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_number", "section", "academic_year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_number", nullable = false)
    private Integer classNumber;

    @Column(name = "section", nullable = false, length = 10)
    @Builder.Default
    private String section = "A";

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 50;

    @Column(name = "current_enrollment")
    @Builder.Default
    private Integer currentEnrollment = 0;

    @Column(name = "class_teacher", length = 100)
    private String classTeacher;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Bidirectional relationship with Student
    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    /**
     * Computed property: Available seats in this class
     * @return Number of available seats
     */
    @Transient
    public Integer getAvailableSeats() {
        return capacity - (currentEnrollment != null ? currentEnrollment : 0);
    }

    /**
     * Check if class has available seats for new enrollment
     * @return true if seats are available
     */
    @Transient
    public boolean hasAvailableSeats() {
        return getAvailableSeats() > 0;
    }

    /**
     * Get display name for this class
     * @return Formatted class name (e.g., "Class 5 - A")
     */
    @Transient
    public String getDisplayName() {
        return "Class " + classNumber + " - " + section;
    }

    /**
     * Check if class is almost full (>80% capacity)
     * @return true if enrollment is above 80%
     */
    @Transient
    public boolean isAlmostFull() {
        if (currentEnrollment == null || capacity == null) return false;
        return (currentEnrollment * 100.0 / capacity) >= 80.0;
    }
}
