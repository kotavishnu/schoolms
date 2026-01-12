package com.schoolms.student.application.service;

import com.schoolms.student.application.mapper.StudentMapper;
import com.schoolms.student.domain.exception.DuplicateAdhaarException;
import com.schoolms.student.domain.exception.DuplicateEmailException;
import com.schoolms.student.domain.exception.DuplicatePhoneException;
import com.schoolms.student.domain.exception.StudentNotFoundException;
import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.infrastructure.cache.RedisCacheManager;
import com.schoolms.student.infrastructure.generator.StudentIdGenerator;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.request.UpdateStudentRequest;
import com.schoolms.student.presentation.dto.response.StudentListResponse;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import com.schoolms.student.presentation.dto.response.StudentStatisticsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Student Application Service (BE-021)
 * Orchestrates use cases and coordinates domain and infrastructure
 */
@Service
@Transactional
public class StudentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(StudentApplicationService.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RedisCacheManager cacheManager;
    private final StudentIdGenerator idGenerator;

    public StudentApplicationService(
        StudentRepository studentRepository,
        StudentMapper studentMapper,
        RedisCacheManager cacheManager,
        StudentIdGenerator idGenerator
    ) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.cacheManager = cacheManager;
        this.idGenerator = idGenerator;
    }

    /**
     * Command: Create Student
     */
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating student: firstName={}, lastName={}, phone={}",
            request.firstName(), request.lastName(), maskPhone(request.phone()));

        // Check uniqueness
        if (studentRepository.existsByPhone(request.phone())) {
            throw new DuplicatePhoneException(request.phone());
        }
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        if (studentRepository.existsByAdhaarNumber(request.adhaarNumber())) {
            throw new DuplicateAdhaarException(request.adhaarNumber());
        }

        // Generate student ID
        String studentId = idGenerator.generateNextStudentId();

        // Create domain entity
        Student student = Student.register(
            studentId,
            request.firstName(),
            request.lastName(),
            request.dateOfBirth(),
            request.adhaarNumber(),
            request.phone(),
            request.email(),
            request.address(),
            request.guardianName(),
            request.motherName(),
            request.identificationMarks()
        );

        // Persist
        student = studentRepository.save(student);

        // Invalidate cache
        cacheManager.evictAllStudentCaches();

        log.info("Student created successfully: studentId={}", student.getId().getValue());

        // Map to response DTO
        return studentMapper.toResponse(student);
    }

    /**
     * Command: Update Student
     */
    public StudentResponse updateStudent(String studentId, UpdateStudentRequest request) {
        log.info("Updating student: studentId={}", studentId);

        Student student = studentRepository.findById(new StudentId(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Check phone uniqueness if changed
        if (request.phone() != null && !request.phone().equals(student.getPhone())) {
            if (studentRepository.existsByPhone(request.phone())) {
                throw new DuplicatePhoneException(request.phone());
            }
        }

        // Update domain entity
        student.updateProfile(request.firstName(), request.lastName(), request.phone());

        if (request.status() != null) {
            if (request.status() == StudentStatus.ACTIVE) {
                student.activate();
            } else {
                student.deactivate();
            }
        }

        // Persist
        student = studentRepository.save(student);

        // Evict cache
        cacheManager.evictStudent(studentId);
        cacheManager.evictAllStudentCaches();

        log.info("Student updated successfully: studentId={}", studentId);

        return studentMapper.toResponse(student);
    }

    /**
     * Command: Delete Student
     */
    public void deleteStudent(String studentId) {
        log.info("Deleting student: studentId={}", studentId);

        StudentId id = new StudentId(studentId);
        if (!studentRepository.findById(id).isPresent()) {
            throw new StudentNotFoundException(studentId);
        }

        studentRepository.delete(id);

        // Evict cache
        cacheManager.evictStudent(studentId);
        cacheManager.evictAllStudentCaches();

        log.info("Student deleted successfully: studentId={}", studentId);
    }

    /**
     * Query: Get Student by ID
     */
    @Transactional(readOnly = true)
    public StudentResponse getStudent(String studentId) {
        log.debug("Fetching student: studentId={}", studentId);

        // Try cache first
        StudentResponse cached = cacheManager.getStudent(studentId, StudentResponse.class);
        if (cached != null) {
            log.debug("Cache hit for student: studentId={}", studentId);
            return cached;
        }

        // Fetch from database
        Student student = studentRepository.findById(new StudentId(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        StudentResponse response = studentMapper.toResponse(student);

        // Cache result
        cacheManager.cacheStudent(studentId, response);

        return response;
    }

    /**
     * Query: List All Students
     */
    @Transactional(readOnly = true)
    public StudentListResponse listStudents(String search, StudentStatus status) {
        log.debug("Listing students: search={}, status={}", search, status);

        List<Student> students;

        if (search != null && !search.isBlank()) {
            students = studentRepository.search(search);
        } else if (status != null) {
            students = studentRepository.findByStatus(status);
        } else {
            students = studentRepository.findAll();
        }

        List<StudentResponse> studentResponses = studentMapper.toResponseList(students);

        long totalCount = studentRepository.count();
        long activeCount = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long inactiveCount = studentRepository.countByStatus(StudentStatus.INACTIVE);

        return new StudentListResponse(
            studentResponses,
            totalCount,
            activeCount,
            inactiveCount
        );
    }

    /**
     * Query: Get Statistics
     */
    @Transactional(readOnly = true)
    public StudentStatisticsResponse getStatistics() {
        log.debug("Fetching student statistics");

        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long inactiveStudents = studentRepository.countByStatus(StudentStatus.INACTIVE);

        return new StudentStatisticsResponse(
            totalStudents,
            activeStudents,
            inactiveStudents
        );
    }

    /**
     * Helper method to mask phone number
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "******" + phone.substring(phone.length() - 4);
    }
}
