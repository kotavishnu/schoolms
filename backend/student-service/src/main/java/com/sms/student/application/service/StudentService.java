package com.sms.student.application.service;

import com.sms.student.presentation.dto.StudentDTO;
import com.sms.student.presentation.dto.CreateStudentRequest;
import org.springframework.data.domain.Page;

/**
 * Service interface for student management business logic.
 * Defines the contract for student operations.
 */
public interface StudentService {

    /**
     * Create a new student.
     * Generates student key, validates age (3-18), checks mobile uniqueness.
     *
     * @param request the request containing student details
     * @return the created student DTO
     * @throws com.sms.shared.exception.ValidationException if age is invalid
     * @throws com.sms.shared.exception.DuplicateResourceException if mobile already exists
     */
    StudentDTO createStudent(CreateStudentRequest request);

    /**
     * Get student by student key.
     *
     * @param studentKey the student business key
     * @return the student DTO
     * @throws com.sms.shared.exception.ResourceNotFoundException if student not found
     */
    StudentDTO getStudent(String studentKey);

    /**
     * Update student by student key.
     * Only allows updating firstName, lastName, mobile, and status.
     *
     * @param studentKey the student business key
     * @param request the update request
     * @return the updated student DTO
     * @throws com.sms.shared.exception.ResourceNotFoundException if student not found
     * @throws com.sms.shared.exception.DuplicateResourceException if mobile is already taken
     */
    StudentDTO updateStudent(String studentKey, CreateStudentRequest request);

    /**
     * Delete a student by student key.
     *
     * @param studentKey the student business key
     * @throws com.sms.shared.exception.ResourceNotFoundException if student not found
     */
    void deleteStudent(String studentKey);

    /**
     * Search students by lastName or fatherNameOrGuardian.
     *
     * @param searchTerm the search term
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return page of students matching the search term
     */
    Page<StudentDTO> searchStudents(String searchTerm, int page, int size);
}
