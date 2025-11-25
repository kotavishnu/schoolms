package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.*;
import org.springframework.data.domain.Pageable;

/**
 * Application service interface for student operations.
 */
public interface StudentService {

    /**
     * Create a new student.
     */
    StudentResponse createStudent(CreateStudentRequest request);

    /**
     * Get student by database ID.
     */
    StudentResponse getStudentById(Long id);

    /**
     * Get student by StudentId (STU-YYYY-XXXXX).
     */
    StudentResponse getStudentByStudentId(String studentId);

    /**
     * Search students with criteria.
     */
    StudentPageResponse searchStudents(StudentSearchCriteria criteria, Pageable pageable);

    /**
     * Update student profile.
     */
    StudentResponse updateStudent(Long id, UpdateStudentRequest request);

    /**
     * Update student status.
     */
    StudentResponse updateStatus(Long id, UpdateStatusRequest request);

    /**
     * Delete student (soft delete by setting status to INACTIVE).
     */
    void deleteStudent(Long id);
}
