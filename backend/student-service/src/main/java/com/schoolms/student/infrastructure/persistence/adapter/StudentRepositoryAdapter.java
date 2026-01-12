package com.schoolms.student.infrastructure.persistence.adapter;

import com.schoolms.student.domain.exception.StudentNotFoundException;
import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import com.schoolms.student.infrastructure.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Student Repository Adapter (BE-015)
 * Implements domain repository using JPA repository
 * Bridges domain layer and infrastructure layer
 */
@Component
public class StudentRepositoryAdapter implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    public StudentRepositoryAdapter(
        StudentJpaRepository jpaRepository,
        StudentEntityMapper entityMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Student save(Student student) {
        // Check if student already exists to preserve database ID
        Optional<StudentJpaEntity> existingEntity = jpaRepository.findByStudentId(student.getId().getValue());

        StudentJpaEntity entity = entityMapper.toJpaEntity(student);

        // Preserve the database ID for updates
        if (existingEntity.isPresent()) {
            entity.setId(existingEntity.get().getId());
        }

        StudentJpaEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public void delete(StudentId studentId) {
        StudentJpaEntity entity = jpaRepository.findByStudentId(studentId.getValue())
            .orElseThrow(() -> new StudentNotFoundException(studentId.getValue()));
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Student> findById(StudentId studentId) {
        return jpaRepository.findByStudentId(studentId.getValue())
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByAdhaarNumber(String adhaarNumber) {
        return jpaRepository.findByAdhaarNumber(adhaarNumber)
            .map(entityMapper::toDomain);
    }

    @Override
    public List<Student> findAll() {
        return jpaRepository.findAll().stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Student> findByStatus(StudentStatus status) {
        return jpaRepository.findByStatus(status).stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Student> search(String query) {
        return jpaRepository.search(query).stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(StudentStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByAdhaarNumber(String adhaarNumber) {
        return jpaRepository.existsByAdhaarNumber(adhaarNumber);
    }
}
