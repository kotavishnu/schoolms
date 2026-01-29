package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for Student table
 * Separate from domain model to maintain clean architecture
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_student_id", columnList = "student_id", unique = true),
    @Index(name = "idx_students_last_name", columnList = "last_name"),
    @Index(name = "idx_students_status", columnList = "status"),
    @Index(name = "idx_students_mobile", columnList = "mobile", unique = true),
    @Index(name = "idx_students_aadhaar", columnList = "aadhaar_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", unique = true, nullable = false, length = 10)
    private String mobile;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "fathers_name", length = 100)
    private String fathersName;

    @Column(name = "mothers_name", length = 100)
    private String mothersName;

    @Column(name = "identification_mark", length = 200)
    private String identificationMark;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<EnrollmentEntity> enrollments = new ArrayList<>();
}
