package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.domain.model.Enrollment;
import com.school.sms.student.domain.repository.EnrollmentRepository;
import com.school.sms.student.infrastructure.persistence.entity.EnrollmentJpaEntity;
import com.school.sms.student.infrastructure.persistence.mapper.EnrollmentEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of EnrollmentRepository using JPA.
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
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository jpaRepository;
    private final EnrollmentEntityMapper mapper;

    @Override
    public Enrollment save(Enrollment enrollment) {
        log.debug("Saving enrollment for student ID: {}, academic year: {}",
                  enrollment.getStudentId(), enrollment.getAcademicYear());

        EnrollmentJpaEntity entity = mapper.toJpaEntity(enrollment);
        EnrollmentJpaEntity savedEntity = jpaRepository.save(entity);
        Enrollment savedEnrollment = mapper.toDomain(savedEntity);

        log.info("Enrollment saved successfully with ID: {}", savedEnrollment.getId());
        return savedEnrollment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findByStudentId(Long studentId) {
        log.debug("Finding enrollments for student ID: {}", studentId);
        return jpaRepository.findByStudentIdOrderByAcademicYearDesc(studentId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enrollment> findByStudentIdAndAcademicYear(Long studentId, String academicYear) {
        log.debug("Finding enrollment for student ID: {}, academic year: {}", studentId, academicYear);
        return jpaRepository.findByStudentIdAndAcademicYear(studentId, academicYear)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findByAcademicYear(String academicYear) {
        log.debug("Finding enrollments for academic year: {}", academicYear);
        return jpaRepository.findByAcademicYear(academicYear)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear) {
        return jpaRepository.existsByStudentIdAndAcademicYear(studentId, academicYear);
    }
}
