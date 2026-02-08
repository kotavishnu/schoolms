package com.school.student.infrastructure.persistence.adapter;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.EnrollmentStatus;
import com.school.student.domain.repository.EnrollmentRepository;
import com.school.student.infrastructure.persistence.entity.EnrollmentEntity;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import com.school.student.infrastructure.persistence.mapper.EnrollmentEntityMapper;
import com.school.student.infrastructure.persistence.repository.JpaEnrollmentRepositoryInterface;
import com.school.student.infrastructure.persistence.repository.JpaStudentRepositoryInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation that bridges domain EnrollmentRepository to JPA persistence.
 * Implements Hexagonal Architecture pattern (Ports and Adapters).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Delegates to Spring Data JPA repository</li>
 *   <li>Converts between domain models (Enrollment) and JPA entities (EnrollmentEntity)</li>
 *   <li>Manages StudentEntity references for foreign key relationships</li>
 *   <li>Validates inputs at the adapter boundary</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaEnrollmentRepositoryAdapter implements EnrollmentRepository {

    private final JpaEnrollmentRepositoryInterface jpaRepository;
    private final JpaStudentRepositoryInterface studentJpaRepository;
    private final EnrollmentEntityMapper mapper;

    @Override
    public Enrollment save(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }

        log.debug("Saving enrollment for student ID: {}, academic year: {}",
                enrollment.getStudentId(), enrollment.getAcademicYear());

        // Convert to entity
        EnrollmentEntity entity = mapper.toEntity(enrollment);

        // Set StudentEntity reference for foreign key
        StudentEntity studentEntity = studentJpaRepository.findById(enrollment.getStudentId())
                .orElseThrow(() -> new IllegalStateException(
                        "Student not found with ID: " + enrollment.getStudentId()));
        entity.setStudent(studentEntity);

        // Save and convert back
        EnrollmentEntity savedEntity = jpaRepository.save(entity);
        Enrollment savedEnrollment = mapper.toDomain(savedEntity);

        log.info("Enrollment saved successfully for student ID: {}, academic year: {}",
                savedEnrollment.getStudentId(), savedEnrollment.getAcademicYear());

        return savedEnrollment;
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        log.debug("Finding enrollment by id: {}", id);

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        log.debug("Finding enrollments for student ID: {}", studentId);

        return jpaRepository.findByStudentId(studentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Enrollment> findByStudentIdAndAcademicYear(Long studentId, String academicYear) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year cannot be null");
        }

        log.debug("Finding enrollment for student ID: {}, academic year: {}", studentId, academicYear);

        return jpaRepository.findByStudentIdAndAcademicYear(studentId, academicYear)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year cannot be null");
        }

        log.debug("Checking if enrollment exists for student ID: {}, academic year: {}",
                studentId, academicYear);

        return jpaRepository.existsByStudentIdAndAcademicYear(studentId, academicYear);
    }

    @Override
    public List<Enrollment> findActiveEnrollmentsByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        log.debug("Finding active enrollments for student ID: {}", studentId);

        return jpaRepository.findActiveEnrollmentsByStudentId(studentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrollment> findByAcademicYear(String academicYear) {
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year cannot be null");
        }

        log.debug("Finding enrollments for academic year: {}", academicYear);

        return jpaRepository.findByAcademicYear(academicYear).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrollment> findByAcademicYearAndStatus(String academicYear, EnrollmentStatus status) {
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        log.debug("Finding enrollments for academic year: {}, status: {}", academicYear, status);

        EnrollmentEntity.EnrollmentStatusEnum entityStatus = toEntityStatus(status);
        return jpaRepository.findByAcademicYearAndStatus(academicYear, entityStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        log.debug("Deleting enrollment by id: {}", id);

        jpaRepository.deleteById(id);

        log.info("Enrollment deleted successfully: {}", id);
    }

    @Override
    public long countByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        log.debug("Counting enrollments for student ID: {}", studentId);

        return jpaRepository.countByStudentId(studentId);
    }

    // Helper method for status conversion
    private EnrollmentEntity.EnrollmentStatusEnum toEntityStatus(EnrollmentStatus status) {
        return switch (status) {
            case ACTIVE -> EnrollmentEntity.EnrollmentStatusEnum.ACTIVE;
            case WITHDRAWN -> EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN;
            case COMPLETED -> EnrollmentEntity.EnrollmentStatusEnum.COMPLETED;
        };
    }
}
