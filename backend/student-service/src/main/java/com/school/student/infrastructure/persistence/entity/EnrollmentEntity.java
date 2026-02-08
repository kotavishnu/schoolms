package com.school.student.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * JPA Entity for enrollments table.
 * This is the infrastructure layer representation for persistence.
 *
 * <p>Mapping Strategy:
 * <ul>
 *   <li>Snake_case column names matching database schema</li>
 *   <li>Optimistic locking with @Version</li>
 *   <li>ManyToOne relationship with StudentEntity</li>
 *   <li>Unique constraint on (student_id, academic_year) for BR-3</li>
 * </ul>
 *
 * <p>Note: This is NOT the domain model. Domain model is {@code Enrollment}.
 * MapStruct will handle conversion between EnrollmentEntity ↔ Enrollment.
 */
@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_academic_year",
            columnNames = {"student_id", "academic_year"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private StudentEntity student;

    @Column(name = "academic_year", nullable = false, length = 10)
    private String academicYear;

    @Column(name = "grade_class", nullable = false, length = 20)
    private String gradeClass;

    @Column(name = "section", nullable = false, length = 10)
    private String section;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatusEnum status;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * JPA lifecycle callback to set creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        if (this.version == null) {
            this.version = 0L;
        }
    }

    /**
     * Enum for enrollment status at JPA entity level.
     * Matches domain EnrollmentStatus enum.
     */
    public enum EnrollmentStatusEnum {
        ACTIVE,
        WITHDRAWN,
        COMPLETED
    }
}
