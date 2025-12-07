package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.model.StudentStatus;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import com.school.sms.student.infrastructure.persistence.mapper.StudentEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of StudentRepository using JPA.
 *
 * <p>This class bridges the domain layer and infrastructure layer,
 * converting between domain models and JPA entities.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper mapper;
    private final AtomicInteger sequenceCounter = new AtomicInteger(1);

    @Override
    public Student save(Student student) {
        log.debug("Saving student: {}", student.getStudentId());

        StudentJpaEntity entity = mapper.toJpaEntity(student);

        // Generate student ID if new student
        if (entity.getId() == null && entity.getStudentId() == null) {
            entity.setStudentId(generateStudentId());
        }

        StudentJpaEntity savedEntity = jpaRepository.save(entity);
        Student savedStudent = mapper.toDomain(savedEntity);

        log.info("Student saved successfully with ID: {}", savedStudent.getStudentId());
        return savedStudent;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        log.debug("Finding student by database ID: {}", id);
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByStudentId(StudentId studentId) {
        log.debug("Finding student by student ID: {}", studentId);
        return jpaRepository.findByStudentId(studentId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByMobile(String mobile) {
        log.debug("Finding student by mobile: {}", mobile);
        return jpaRepository.findByMobile(mobile)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Student> findByLastName(String lastName, Pageable pageable) {
        log.debug("Finding students by last name: {}", lastName);
        return jpaRepository.findByLastNameContainingIgnoreCase(lastName, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Student> findByFathersName(String fathersName, Pageable pageable) {
        log.debug("Finding students by father's name: {}", fathersName);
        return jpaRepository.findByFathersNameContainingIgnoreCase(fathersName, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Student> findByStatus(StudentStatus status, Pageable pageable) {
        log.debug("Finding students by status: {}", status);
        StudentJpaEntity.StudentStatusEnum jpaStatus =
            StudentJpaEntity.StudentStatusEnum.valueOf(status.name());
        return jpaRepository.findByStatus(jpaStatus, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Student> findAll(Pageable pageable) {
        log.debug("Finding all students with pagination");
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting student with ID: {}", id);
        jpaRepository.deleteById(id);
        log.info("Student deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMobile(String mobile) {
        return jpaRepository.existsByMobile(mobile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAadhaarNumber(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.isBlank()) {
            return false;
        }
        return jpaRepository.existsByAadhaarNumber(aadhaarNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveStudents() {
        return jpaRepository.countActiveStudents();
    }

    /**
     * Generates a unique Student ID in the format: STD-YYYYMMDD-NNNN
     *
     * <p>Format breakdown:</p>
     * <ul>
     *   <li>STD: Fixed prefix</li>
     *   <li>YYYYMMDD: Current date</li>
     *   <li>NNNN: Sequential number (4 digits, padded with zeros)</li>
     * </ul>
     *
     * @return a generated student ID
     */
    private String generateStudentId() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = sequenceCounter.getAndIncrement();
        String sequenceStr = String.format("%04d", sequence % 10000);

        String studentId = String.format("STD-%s-%s", dateStr, sequenceStr);

        log.debug("Generated student ID: {}", studentId);
        return studentId;
    }
}
