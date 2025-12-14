package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.model.Mobile;
import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Domain repository interface for Student aggregate.
 * This is a port in hexagonal architecture.
 * Implementation will be in infrastructure layer.
 */
public interface StudentRepository {

    /**
     * Save a student.
     */
    Student save(Student student);

    /**
     * Find student by database ID.
     */
    Optional<Student> findById(Long id);

    /**
     * Find student by StudentId value object.
     */
    Optional<Student> findByStudentId(StudentId studentId);

    /**
     * Find student by Mobile value object.
     */
    Optional<Student> findByMobile(Mobile mobile);

    /**
     * Find all students with pagination.
     */
    Page<Student> findAll(Pageable pageable);

    /**
     * Search students by last name (partial match, case-insensitive).
     */
    Page<Student> findByLastNameContaining(String lastName, Pageable pageable);

    /**
     * Search students by father/guardian name (partial match, case-insensitive).
     */
    Page<Student> findByFatherNameContaining(String fatherName, Pageable pageable);

    /**
     * Find students by status.
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Check if mobile number already exists.
     */
    boolean existsByMobile(Mobile mobile);

    /**
     * Count students with StudentId starting with prefix (for ID generation).
     */
    long countByStudentIdStartingWith(String prefix);

    /**
     * Delete student by ID (used for testing cleanup).
     */
    void deleteById(Long id);
}
