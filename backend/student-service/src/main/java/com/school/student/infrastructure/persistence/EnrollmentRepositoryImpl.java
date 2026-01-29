package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of enrollment repository using Spring Data JPA
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository jpaRepository;
    private final EnrollmentEntityMapper entityMapper;
    private final StudentJpaRepository studentJpaRepository;

    @Override
    public Enrollment save(Enrollment enrollment) {
        log.debug("Saving enrollment for student ID: {}", enrollment.getStudentId());

        EnrollmentEntity entity = entityMapper.toEntity(enrollment);

        // Set student reference for JPA relationship
        StudentEntity student = studentJpaRepository.findById(enrollment.getStudentId())
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + enrollment.getStudentId()));
        entity.setStudent(student);

        EnrollmentEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        log.debug("Finding enrollments for student ID: {}", studentId);
        return jpaRepository.findByStudentId(studentId)
            .stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear) {
        log.debug("Checking enrollment existence for student {} in year {}", studentId, academicYear);
        return jpaRepository.existsByStudentIdAndAcademicYear(studentId, academicYear);
    }
}
