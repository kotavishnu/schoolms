package com.school.sms.student.application.service;

import com.school.sms.common.exception.DuplicateResourceException;
import com.school.sms.common.exception.ResourceNotFoundException;
import com.school.sms.student.application.dto.*;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.domain.model.*;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.service.StudentIdGenerator;
import com.school.sms.student.infrastructure.cache.StudentCacheService;
import com.school.sms.student.infrastructure.metrics.StudentMetrics;
import com.school.sms.student.infrastructure.rules.RulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of StudentService.
 * Orchestrates business operations across domain, infrastructure layers.
 */
@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentIdGenerator studentIdGenerator;
    private final RulesService rulesService;
    private final StudentCacheService cacheService;
    private final StudentMapper studentMapper;
    private final StudentMetrics studentMetrics;

    public StudentServiceImpl(StudentRepository studentRepository,
                            StudentIdGenerator studentIdGenerator,
                            RulesService rulesService,
                            StudentCacheService cacheService,
                            StudentMapper studentMapper,
                            StudentMetrics studentMetrics) {
        this.studentRepository = studentRepository;
        this.studentIdGenerator = studentIdGenerator;
        this.rulesService = rulesService;
        this.cacheService = cacheService;
        this.studentMapper = studentMapper;
        this.studentMetrics = studentMetrics;
    }

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating student: {} {}", request.getFirstName(), request.getLastName());

        // Validate via Drools
        rulesService.validateStudentRegistration(request);

        // Check mobile uniqueness
        Mobile mobile = Mobile.of(request.getMobile());
        if (studentRepository.existsByMobile(mobile)) {
            throw new DuplicateResourceException("Student", "mobile", request.getMobile());
        }

        // Generate StudentId
        StudentId studentId = studentIdGenerator.generateNext();

        // Create Student aggregate
        Student student = Student.register(
            studentId,
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            mobile,
            request.getAddress(),
            request.getFatherName(),
            request.getMotherName(),
            request.getIdentificationMark(),
            request.getAadhaarNumber(),
            request.getEmail()
        );

        // Save to repository
        Student savedStudent = studentRepository.save(student);

        // Track metrics
        studentMetrics.incrementRegistered();

        log.info("Student created successfully with ID: {}", savedStudent.getStudentId().getValue());

        return studentMapper.toResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        log.debug("Fetching student by ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getStudentByStudentId(String studentId) {
        log.debug("Fetching student by StudentId: {}", studentId);

        // Check cache first
        Student cached = cacheService.getStudent(studentId);
        if (cached != null) {
            log.debug("Student found in cache: {}", studentId);
            return studentMapper.toResponse(cached);
        }

        // Fetch from DB on cache miss
        StudentId sid = StudentId.of(studentId);
        Student student = studentRepository.findByStudentId(sid)
            .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        return studentMapper.toResponse(student);
    }

    @Override
    public StudentPageResponse searchStudents(StudentSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching students with criteria: {}", criteria);

        Page<Student> studentPage;

        if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
            studentPage = studentRepository.findByLastNameContaining(criteria.getLastName(), pageable);
        } else if (criteria.getGuardianName() != null && !criteria.getGuardianName().isBlank()) {
            studentPage = studentRepository.findByFatherNameContaining(criteria.getGuardianName(), pageable);
        } else if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
            StudentStatus status = StudentStatus.valueOf(criteria.getStatus().toUpperCase());
            studentPage = studentRepository.findByStatus(status, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        return studentMapper.toPageResponse(studentPage);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        log.info("Updating student ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        // Check mobile uniqueness if changed
        Mobile newMobile = Mobile.of(request.getMobile());
        if (!student.getMobile().equals(newMobile)) {
            if (studentRepository.existsByMobile(newMobile)) {
                throw new DuplicateResourceException("Student", "mobile", request.getMobile());
            }
        }

        // Update via domain method
        student.updateProfile(request.getFirstName(), request.getLastName(), newMobile);

        Student updated = studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(updated.getStudentId().getValue());

        // Track metrics
        studentMetrics.incrementUpdated();

        log.info("Student updated successfully: {}", updated.getStudentId().getValue());

        return studentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public StudentResponse updateStatus(Long id, UpdateStatusRequest request) {
        log.info("Updating status for student ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        StudentStatus oldStatus = student.getStatus();
        StudentStatus newStatus = StudentStatus.valueOf(request.getStatus().toUpperCase());
        student.changeStatus(newStatus);

        Student updated = studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(updated.getStudentId().getValue());

        // Track metrics
        studentMetrics.incrementStatusChanged(oldStatus.name(), newStatus.name());

        log.info("Student status updated to: {}", newStatus);

        return studentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        // Soft delete by setting status to INACTIVE
        StudentStatus oldStatus = student.getStatus();
        student.changeStatus(StudentStatus.INACTIVE);
        studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(student.getStudentId().getValue());

        // Track metrics
        studentMetrics.incrementStatusChanged(oldStatus.name(), StudentStatus.INACTIVE.name());

        log.info("Student soft deleted: {}", student.getStudentId().getValue());
    }
}
