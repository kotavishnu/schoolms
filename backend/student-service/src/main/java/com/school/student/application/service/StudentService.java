package com.school.student.application.service;

import com.school.student.application.mapper.StudentDTOMapper;
import com.school.student.common.exception.ValidationException;
import com.school.student.common.validation.ValidationResult;
import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import com.school.student.presentation.dto.StudentUpdateRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service for student management operations.
 * Orchestrates business logic, validation, and persistence.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Map DTOs to domain models</li>
 *   <li>Execute Drools business rule validation</li>
 *   <li>Coordinate with repository for persistence</li>
 *   <li>Generate student IDs</li>
 *   <li>Handle transactions</li>
 * </ul>
 *
 * <p>All write operations are transactional.
 */
@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final Optional<KieContainer> kieContainer;
    private final StudentDTOMapper studentDTOMapper;
    private final StudentIdGenerator studentIdGenerator;
    private final com.school.student.infrastructure.metrics.StudentMetrics studentMetrics;

    @Autowired
    public StudentService(
            StudentRepository studentRepository,
            @Autowired(required = false) KieContainer kieContainer,
            StudentDTOMapper studentDTOMapper,
            StudentIdGenerator studentIdGenerator,
            com.school.student.infrastructure.metrics.StudentMetrics studentMetrics) {
        this.studentRepository = studentRepository;
        this.kieContainer = Optional.ofNullable(kieContainer);
        this.studentDTOMapper = studentDTOMapper;
        this.studentIdGenerator = studentIdGenerator;
        this.studentMetrics = studentMetrics;

        if (this.kieContainer.isPresent()) {
            log.info("Drools validation is ENABLED");
        } else {
            log.warn("Drools validation is DISABLED - using Spring validation only");
        }
    }

    /**
     * Register a new student.
     * Executes Drools validation before persistence.
     *
     * @param requestDTO student registration data
     * @return saved student as response DTO
     * @throws ValidationException if business rules fail
     */
    @Transactional
    public StudentResponseDTO registerStudent(StudentRequestDTO requestDTO) {
        log.info("Registering new student: {} {}", requestDTO.firstName(), requestDTO.lastName());

        // 1. Map DTO to domain model
        Student student = studentDTOMapper.toDomain(requestDTO);

        // 2. Execute Drools validation
        validateWithDrools(student);

        // 3. Generate student ID
        String studentId = studentIdGenerator.generate();
        student.setStudentId(studentId);

        log.debug("Assigned student ID: {}", studentId);

        // 4. Save student
        Student savedStudent = studentRepository.save(student);

        log.info("Student registered successfully: {}", savedStudent.getStudentId());

        // 5. Return response DTO
        return studentDTOMapper.toResponseDTO(savedStudent);
    }

    /**
     * Get student by student ID.
     *
     * @param studentId the unique business key
     * @return student data
     * @throws RuntimeException if student not found
     */
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(String studentId) {
        log.debug("Fetching student by ID: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        return studentDTOMapper.toResponseDTO(student);
    }

    /**
     * Update student profile.
     * Checks optimistic locking version.
     *
     * @param studentId the student ID
     * @param updateDTO update data with version
     * @return updated student
     * @throws RuntimeException if student not found or version mismatch
     */
    @Transactional
    public StudentResponseDTO updateStudent(String studentId, StudentUpdateRequestDTO updateDTO) {
        log.info("Updating student: {}", studentId);

        // 1. Fetch existing student
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // 2. Check version for optimistic locking
        if (!student.getVersion().equals(updateDTO.version())) {
            throw new RuntimeException("Optimistic locking failure: version mismatch");
        }

        // 3. Update allowed fields using domain method
        if (updateDTO.firstName() != null || updateDTO.lastName() != null ||
            updateDTO.mobile() != null) {

            // Use domain updateProfile method
            student.updateProfile(
                updateDTO.firstName() != null ? updateDTO.firstName() : student.getFirstName(),
                updateDTO.lastName() != null ? updateDTO.lastName() : student.getLastName(),
                updateDTO.mobile() != null ? com.school.student.domain.model.valueobject.Mobile.of(updateDTO.mobile()) : student.getMobile()
            );
        }

        // 4. Update optional fields directly
        if (updateDTO.email() != null) {
            student.setEmail(updateDTO.email());
        }
        if (updateDTO.address() != null) {
            student.setAddress(updateDTO.address());
        }
        if (updateDTO.identificationMark() != null) {
            student.setIdentificationMark(updateDTO.identificationMark());
        }

        // 5. Handle status change
        if (updateDTO.status() != null) {
            StudentStatus newStatus = StudentStatus.valueOf(updateDTO.status());
            if (newStatus == StudentStatus.INACTIVE && student.getStatus() == StudentStatus.ACTIVE) {
                student.deactivate();
            } else if (newStatus == StudentStatus.ACTIVE && student.getStatus() == StudentStatus.INACTIVE) {
                student.activate();
            }
        }

        // 6. Validate with Drools
        validateWithDrools(student);

        // 7. Save
        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully: {}", studentId);

        return studentDTOMapper.toResponseDTO(updatedStudent);
    }

    /**
     * Delete student by ID.
     *
     * @param studentId the student ID
     */
    @Transactional
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        studentRepository.deleteById(student.getId());

        log.info("Student deleted successfully: {}", studentId);
    }

    /**
     * Search students by last name.
     *
     * @param lastName last name to search
     * @param pageable pagination parameters
     * @return page of students
     */
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchByLastName(String lastName, Pageable pageable) {
        log.debug("Searching students by last name: {}", lastName);

        Page<Student> students = studentRepository.findByLastNameContaining(lastName, pageable);
        return students.map(studentDTOMapper::toResponseDTO);
    }

    /**
     * Find students by status.
     *
     * @param status the student status
     * @param pageable pagination parameters
     * @return page of students
     */
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> findByStatus(StudentStatus status, Pageable pageable) {
        log.debug("Finding students by status: {}", status);

        Page<Student> students = studentRepository.findByStatus(status, pageable);
        return students.map(studentDTOMapper::toResponseDTO);
    }

    /**
     * Get all students with pagination.
     *
     * @param pageable pagination parameters
     * @return page of students
     */
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        log.debug("Fetching all students, page: {}", pageable.getPageNumber());

        Page<Student> students = studentRepository.findAll(pageable);
        return students.map(studentDTOMapper::toResponseDTO);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Execute Drools business rule validation.
     * Throws ValidationException if rules fail.
     * If Drools is not available, logs warning and skips validation.
     *
     * @param student the student to validate
     * @throws ValidationException if validation fails
     */
    private void validateWithDrools(Student student) {
        if (kieContainer.isEmpty()) {
            log.debug("Drools not available - skipping business rule validation");
            return;
        }

        KieSession kieSession = kieContainer.get().newKieSession();

        try {
            ValidationResult validationResult = new ValidationResult();

            // Insert facts into session
            kieSession.insert(validationResult);
            kieSession.insert(student);
            kieSession.setGlobal("studentRepository", studentRepository);

            // Fire all rules
            int rulesFired = kieSession.fireAllRules();
            log.debug("Drools validation executed: {} rules fired", rulesFired);

            // Check validation result
            if (!validationResult.isValid()) {
                log.warn("Drools validation failed: {}", validationResult);
                throw new ValidationException(validationResult);
            }

            log.debug("Drools validation passed");

        } finally {
            // Always dispose session
            kieSession.dispose();
        }
    }
}
