package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.exception.DuplicateMobileException;
import com.school.sms.student.domain.exception.InvalidAgeException;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.infrastructure.util.StudentIdGenerator;
import com.school.sms.student.presentation.dto.CreateStudentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service for student registration operations.
 * Handles new student creation with business rule validation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentRegistrationService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final StudentIdGenerator idGenerator;

    private static final int MIN_AGE = 3;
    private static final int MAX_AGE = 18;

    /**
     * Registers a new student.
     *
     * @param request the create student request
     * @param userId the user performing the operation
     * @return StudentDTO of the registered student
     * @throws InvalidAgeException if age is not between 3-18 years
     * @throws DuplicateMobileException if mobile number already exists
     */
    public StudentDTO registerStudent(CreateStudentRequest request, String userId) {
        log.info("Registering new student: {} {}", request.getFirstName(), request.getLastName());

        // Validate age (Business Rule BR-1)
        validateAge(request.getDateOfBirth());

        // Check mobile uniqueness (Business Rule BR-2)
        if (studentRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateMobileException(
                "Mobile number already registered: " + request.getMobile());
        }

        // Generate student ID
        String studentId = idGenerator.generate();

        // Create entity
        Student student = studentMapper.toEntity(request);
        student.setStudentId(studentId);
        student.setCreatedBy(userId);
        student.setUpdatedBy(userId);

        // Save
        Student saved = studentRepository.save(student);

        log.info("Student registered successfully: {}", saved.getStudentId());
        return studentMapper.toDTO(saved);
    }

    private void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new InvalidAgeException(
                String.format("Student age must be between %d and %d years. Provided age: %d",
                    MIN_AGE, MAX_AGE, age));
        }
    }
}
