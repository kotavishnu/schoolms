package com.sms.student.infrastructure.persistence.entity;

import com.sms.student.domain.model.StudentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity for Student persistence.
 * Maps to the student table in the database.
 */
@Entity
@Table(name = "student", indexes = {
    @Index(name = "idx_student_last_name", columnList = "last_name"),
    @Index(name = "idx_student_guardian", columnList = "father_name_or_guardian"),
    @Index(name = "idx_student_mobile", columnList = "mobile"),
    @Index(name = "idx_student_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "student_key", unique = true, nullable = false, length = 50)
    private String studentKey;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", unique = true, nullable = false, length = 15)
    private String mobile;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "father_name_or_guardian", length = 100)
    private String fatherNameOrGuardian;

    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "identification_mark", length = 200)
    private String identificationMark;

    @Column(name = "adhaar_number", unique = true, length = 12)
    private String adhaarNumber;

    @Convert(converter = com.sms.student.infrastructure.persistence.converter.StudentStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
