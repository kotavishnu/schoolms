package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.valueobject.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface for Student entity.
 * Extends Spring Data JPA repository for standard CRUD operations.
 */
@Repository
public interface JpaStudentRepository extends JpaRepository<Student, String>,
        JpaSpecificationExecutor<Student> {

    /**
     * Checks if a student exists with the given mobile number.
     *
     * @param mobile the mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Checks if a student exists with the given mobile number excluding a specific student ID.
     *
     * @param mobile the mobile number
     * @param studentId the student ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByMobileAndStudentIdNot(String mobile, String studentId);

    /**
     * Finds students by status with pagination.
     *
     * @param status the student status
     * @param pageable pagination information
     * @return page of students
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Finds students by last name (starts with) with pagination.
     *
     * @param lastName the last name prefix
     * @param pageable pagination information
     * @return page of students
     */
    Page<Student> findByLastNameStartingWithIgnoreCase(String lastName, Pageable pageable);
}
