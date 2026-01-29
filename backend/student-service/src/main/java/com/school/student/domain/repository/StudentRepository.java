package com.school.student.domain.repository;

import com.school.student.domain.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Domain repository contract for Student aggregate
 * Implementations in infrastructure layer
 */
public interface StudentRepository {

    /**
     * Save or update a student
     */
    Student save(Student student);

    /**
     * Find student by internal ID
     */
    Optional<Student> findById(Long id);

    /**
     * Find student by business identifier (STD-YYYYMMDD-NNNN)
     */
    Optional<Student> findByStudentId(String studentId);

    /**
     * Search students with filters and pagination
     */
    Page<Student> searchStudents(String lastName, String status, Pageable pageable);

    /**
     * Check if mobile number already exists
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if Aadhaar number already exists
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Count students created on a specific date (for ID generation)
     */
    long countByCreatedAtDate(LocalDate date);

    /**
     * Delete a student
     */
    void delete(Student student);
}
