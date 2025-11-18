package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.valueobject.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for student search and retrieval operations.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StudentSearchService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Finds a student by ID.
     *
     * @param studentId the student ID
     * @return StudentDTO
     * @throws StudentNotFoundException if student not found
     */
    public StudentDTO findById(String studentId) {
        log.debug("Finding student by ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        return studentMapper.toDTO(student);
    }

    /**
     * Finds all students with pagination.
     *
     * @param pageable pagination information
     * @return page of StudentDTOs
     */
    public Page<StudentDTO> findAll(Pageable pageable) {
        log.debug("Finding all students with pagination: {}", pageable);

        Page<Student> students = studentRepository.findAll(pageable);
        return students.map(studentMapper::toDTO);
    }

    /**
     * Finds students by status with pagination.
     *
     * @param status the student status
     * @param pageable pagination information
     * @return page of StudentDTOs
     */
    public Page<StudentDTO> findByStatus(StudentStatus status, Pageable pageable) {
        log.debug("Finding students by status: {} with pagination: {}", status, pageable);

        Page<Student> students = studentRepository.findByStatus(status, pageable);
        return students.map(studentMapper::toDTO);
    }

    /**
     * Finds students by last name (starts with) with pagination.
     *
     * @param lastName the last name prefix
     * @param pageable pagination information
     * @return page of StudentDTOs
     */
    public Page<StudentDTO> findByLastName(String lastName, Pageable pageable) {
        log.debug("Finding students by last name: {} with pagination: {}", lastName, pageable);

        Page<Student> students = studentRepository.findByLastNameStartingWithIgnoreCase(lastName, pageable);
        return students.map(studentMapper::toDTO);
    }
}
