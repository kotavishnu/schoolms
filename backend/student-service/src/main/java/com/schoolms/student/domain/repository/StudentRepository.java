package com.schoolms.student.domain.repository;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.model.StudentStatus;
import java.util.List;
import java.util.Optional;

/**
 * Student Repository Interface (BE-010)
 * Defines domain repository contract
 * Implementation is in the Infrastructure layer
 */
public interface StudentRepository {

    // Commands
    Student save(Student student);
    void delete(StudentId studentId);

    // Queries
    Optional<Student> findById(StudentId studentId);
    Optional<Student> findByPhone(String phone);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByAdhaarNumber(String adhaarNumber);
    List<Student> findAll();
    List<Student> findByStatus(StudentStatus status);
    List<Student> search(String query);

    // Statistics
    long countByStatus(StudentStatus status);
    long count();

    // Existence checks
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByAdhaarNumber(String adhaarNumber);
}
