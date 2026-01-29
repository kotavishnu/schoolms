package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementation of domain repository using Spring Data JPA
 * Acts as adapter between domain and infrastructure layers
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    @Override
    public Student save(Student student) {
        log.debug("Saving student to database: {}", student.getStudentId());
        StudentEntity entity = entityMapper.toEntity(student);
        StudentEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findById(Long id) {
        log.debug("Finding student by ID: {}", id);
        return jpaRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        log.debug("Finding student by student ID: {}", studentId);
        return jpaRepository.findByStudentId(studentId)
            .map(entityMapper::toDomain);
    }

    @Override
    public Page<Student> searchStudents(String lastName, String status, Pageable pageable) {
        log.debug("Searching students with lastName={}, status={}", lastName, status);
        StudentStatus statusEnum = status != null ? StudentStatus.valueOf(status) : null;
        return jpaRepository.searchStudents(lastName, statusEnum, pageable)
            .map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        log.debug("Checking if mobile exists: {}", mobile);
        return jpaRepository.existsByMobile(mobile);
    }

    @Override
    public boolean existsByAadhaarNumber(String aadhaar) {
        log.debug("Checking if Aadhaar exists: {}", aadhaar);
        return jpaRepository.existsByAadhaarNumber(aadhaar);
    }

    @Override
    public long countByCreatedAtDate(LocalDate date) {
        log.debug("Counting students created on: {}", date);
        return jpaRepository.countByCreatedAtDate(date);
    }

    @Override
    public void delete(Student student) {
        log.debug("Deleting student: {}", student.getStudentId());
        jpaRepository.deleteById(student.getId());
    }
}
