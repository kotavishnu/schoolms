package com.sms.student.application.service;

import com.sms.shared.exception.DuplicateResourceException;
import com.sms.shared.exception.ResourceNotFoundException;
import com.sms.shared.exception.ValidationException;
import com.sms.shared.util.DateTimeUtils;
import com.sms.student.application.mapper.StudentMapper;
import com.sms.student.domain.model.Student;
import com.sms.student.domain.model.StudentStatus;
import com.sms.student.infrastructure.persistence.entity.StudentEntity;
import com.sms.student.infrastructure.persistence.repository.StudentRepository;
import com.sms.student.presentation.dto.CreateStudentRequest;
import com.sms.student.presentation.dto.StudentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

/**
 * Implementation of StudentService.
 * Handles business logic for student management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final int MINIMUM_AGE = 3;
    private static final int MAXIMUM_AGE = 18;

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentDTO createStudent(CreateStudentRequest request) {
        log.info("Creating new student: {}", request.firstName());

        // Validate age (3-18 years) - MUST come first
        validateAge(request.dateOfBirth());

        // Check mobile uniqueness
        if (studentRepository.existsByMobile(request.mobile())) {
            throw new DuplicateResourceException("Student", "mobile", request.mobile());
        }

        // Generate student key (STU-YYYY-NNNN)
        String studentKey = generateStudentKey();

        // Create domain model - validation already passed
        Student student = studentMapper.requestToDomain(request);
        if (student != null) {
            student.setStudentKey(studentKey);
            student.setStatus(StudentStatus.ACTIVE);
            student.setCreatedAt(LocalDateTime.now());
            student.setUpdatedAt(LocalDateTime.now());

            // Convert to entity and save
            StudentEntity entity = studentMapper.domainToEntity(student);
            StudentEntity savedEntity = studentRepository.save(entity);

            log.info("Student created successfully with key: {}", studentKey);
            return studentMapper.entityToDTO(savedEntity);
        }

        throw new ValidationException("Failed to create student domain model");
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudent(String studentKey) {
        log.info("Fetching student with key: {}", studentKey);

        StudentEntity entity = studentRepository.findByStudentKey(studentKey)
            .orElseThrow(() -> new ResourceNotFoundException("Student", studentKey));

        return studentMapper.entityToDTO(entity);
    }

    @Override
    public StudentDTO updateStudent(String studentKey, CreateStudentRequest request) {
        log.info("Updating student with key: {}", studentKey);

        StudentEntity entity = studentRepository.findByStudentKey(studentKey)
            .orElseThrow(() -> new ResourceNotFoundException("Student", studentKey));

        // Check if mobile is being updated and if it's already taken by another student
        if (!entity.getMobile().equals(request.mobile()) && studentRepository.existsByMobile(request.mobile())) {
            throw new DuplicateResourceException("Student", "mobile", request.mobile());
        }

        // Update allowed fields
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setMobile(request.mobile());
        entity.setUpdatedAt(LocalDateTime.now());

        StudentEntity updatedEntity = studentRepository.save(entity);
        log.info("Student updated successfully: {}", studentKey);

        return studentMapper.entityToDTO(updatedEntity);
    }

    @Override
    public void deleteStudent(String studentKey) {
        log.info("Deleting student with key: {}", studentKey);

        StudentEntity entity = studentRepository.findByStudentKey(studentKey)
            .orElseThrow(() -> new ResourceNotFoundException("Student", studentKey));

        studentRepository.delete(entity);
        log.info("Student deleted successfully: {}", studentKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> searchStudents(String searchTerm, int page, int size) {
        log.info("Searching students with term: '{}', page: {}, size: {}", searchTerm, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<StudentEntity> result = studentRepository.searchByLastNameOrGuardian(searchTerm, searchTerm, pageable);

        return result.map(studentMapper::entityToDTO);
    }

    /**
     * Validate that student age is within acceptable range (3-18 years).
     *
     * @param dateOfBirth the date of birth
     * @throws ValidationException if age is not between 3 and 18
     */
    private void validateAge(LocalDate dateOfBirth) {
        if (!DateTimeUtils.isAgeInRange(dateOfBirth, MINIMUM_AGE, MAXIMUM_AGE)) {
            int age = DateTimeUtils.calculateAge(dateOfBirth);
            throw new ValidationException(
                String.format("Student age must be between %d and %d years. Current age: %d",
                    MINIMUM_AGE, MAXIMUM_AGE, age)
            );
        }
    }

    /**
     * Generate unique student key in format: STU-YYYY-NNNN
     * where YYYY is current year and NNNN is sequence number.
     *
     * @return generated student key
     */
    private String generateStudentKey() {
        String yearPrefix = "STU-" + Year.now().getValue() + "-";
        Integer maxSequence = studentRepository.findMaxSequenceForYear(yearPrefix);
        int nextSequence = (maxSequence != null ? maxSequence : 0) + 1;
        return String.format("%s%04d", yearPrefix, nextSequence);
    }
}
