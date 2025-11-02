package com.school.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a student enrolled in the school.
 *
 * Database Table: students
 *
 * Business Rules:
 * - Mobile number must be unique (10 digits)
 * - Age must be between 3 and 18 years
 * - Must be assigned to a valid class
 * - Parent details (mother/father name) are mandatory
 *
 * @author School Management Team
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_mobile", columnList = "mobile", unique = true),
    @Index(name = "idx_class_id", columnList = "class_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_name", columnList = "first_name, last_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false, length = 200)
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;

    @Column(name = "caste", length = 50)
    private String caste;

    @Column(name = "mobile", nullable = false, length = 15, unique = true)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Mobile number must be 10 digits starting with 6, 7, 8, or 9")
    private String mobile;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "moles_on_body", length = 200)
    private String molesOnBody;

    @Column(name = "mother_name", nullable = false, length = 100)
    @NotBlank(message = "Mother's name is required")
    @Size(max = 100, message = "Mother's name must not exceed 100 characters")
    private String motherName;

    @Column(name = "father_name", nullable = false, length = 100)
    @NotBlank(message = "Father's name is required")
    @Size(max = 100, message = "Father's name must not exceed 100 characters")
    private String fatherName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SchoolClass schoolClass;

    @Column(name = "enrollment_date", nullable = false)
    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Bidirectional relationships
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<FeeJournal> feeJournals = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<FeeReceipt> feeReceipts = new ArrayList<>();

    /**
     * Get student's full name
     * @return Concatenated first and last name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate student's current age
     * @return Age in years
     */
    @Transient
    public Integer getAge() {
        if (dateOfBirth == null) return null;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Generate roll number based on class and sequence
     * Format: {ClassNumber}{Section}-{ID}
     * Example: 5A-15
     * @return Roll number string
     */
    @Transient
    public String getRollNumber() {
        if (schoolClass == null || id == null) return null;
        return schoolClass.getClassNumber() + schoolClass.getSection() + "-" + String.format("%02d", id);
    }

    /**
     * Check if student is active
     * @return true if status is ACTIVE
     */
    @Transient
    public boolean isActive() {
        return StudentStatus.ACTIVE.equals(status);
    }
}
