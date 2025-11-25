package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Adapter implementation of StudentRepository using JPA.
 * Bridges domain layer and infrastructure layer (Hexagonal Architecture).
 */
@Component
public class StudentRepositoryAdapter implements StudentRepository {

    private final JpaStudentRepository jpaRepository;

    public StudentRepositoryAdapter(JpaStudentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Student save(Student student) {
        StudentJpaEntity entity = toJpaEntity(student);
        StudentJpaEntity saved = jpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id)
            .map(this::toDomainModel);
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        return jpaRepository.findByStudentId(studentId.getValue())
            .map(this::toDomainModel);
    }

    @Override
    public Optional<Student> findByMobile(Mobile mobile) {
        return jpaRepository.findByMobile(mobile.getNumber())
            .map(this::toDomainModel);
    }

    @Override
    public Page<Student> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
            .map(this::toDomainModel);
    }

    @Override
    public Page<Student> findByLastNameContaining(String lastName, Pageable pageable) {
        return jpaRepository.findByLastNameContainingIgnoreCase(lastName, pageable)
            .map(this::toDomainModel);
    }

    @Override
    public Page<Student> findByFatherNameContaining(String fatherName, Pageable pageable) {
        return jpaRepository.findByFatherNameContainingIgnoreCase(fatherName, pageable)
            .map(this::toDomainModel);
    }

    @Override
    public Page<Student> findByStatus(StudentStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status.name(), pageable)
            .map(this::toDomainModel);
    }

    @Override
    public boolean existsByMobile(Mobile mobile) {
        return jpaRepository.existsByMobile(mobile.getNumber());
    }

    @Override
    public long countByStudentIdStartingWith(String prefix) {
        return jpaRepository.countByStudentIdStartingWith(prefix);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // Mapping methods

    private StudentJpaEntity toJpaEntity(Student student) {
        return StudentJpaEntity.builder()
            .id(student.getId())
            .studentId(student.getStudentId() != null ? student.getStudentId().getValue() : null)
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .dateOfBirth(student.getDateOfBirth())
            .mobile(student.getMobile() != null ? student.getMobile().getNumber() : null)
            .address(student.getAddress())
            .fatherName(student.getFatherName())
            .motherName(student.getMotherName())
            .identificationMark(student.getIdentificationMark())
            .aadhaarNumber(student.getAadhaarNumber())
            .email(student.getEmail())
            .status(student.getStatus() != null ? student.getStatus().name() : null)
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .version(student.getVersion())
            .build();
    }

    private Student toDomainModel(StudentJpaEntity entity) {
        return Student.builder()
            .id(entity.getId())
            .studentId(StudentId.of(entity.getStudentId()))
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .dateOfBirth(entity.getDateOfBirth())
            .mobile(Mobile.of(entity.getMobile()))
            .address(entity.getAddress())
            .fatherName(entity.getFatherName())
            .motherName(entity.getMotherName())
            .identificationMark(entity.getIdentificationMark())
            .aadhaarNumber(entity.getAadhaarNumber())
            .email(entity.getEmail())
            .status(StudentStatus.valueOf(entity.getStatus()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
