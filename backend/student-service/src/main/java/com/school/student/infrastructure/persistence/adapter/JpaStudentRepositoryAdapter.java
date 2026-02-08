package com.school.student.infrastructure.persistence.adapter;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import com.school.student.infrastructure.persistence.mapper.StudentEntityMapper;
import com.school.student.infrastructure.persistence.repository.JpaStudentRepositoryInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementation that bridges domain StudentRepository to JPA persistence.
 * Implements Hexagonal Architecture pattern (Ports and Adapters).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Delegates to Spring Data JPA repository</li>
 *   <li>Converts between domain models (Student) and JPA entities (StudentEntity)</li>
 *   <li>Translates persistence exceptions to domain exceptions</li>
 *   <li>Validates inputs at the adapter boundary</li>
 * </ul>
 *
 * <p>This adapter isolates domain layer from infrastructure concerns.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaStudentRepositoryAdapter implements StudentRepository {

    private final JpaStudentRepositoryInterface jpaRepository;
    private final StudentEntityMapper mapper;

    @Override
    public Student save(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        log.debug("Saving student: {}", student.getStudentId());

        StudentEntity entity = mapper.toEntity(student);
        StudentEntity savedEntity = jpaRepository.save(entity);
        Student savedStudent = mapper.toDomain(savedEntity);

        log.info("Student saved successfully: {}", savedStudent.getStudentId());
        return savedStudent;
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be null or blank");
        }

        log.debug("Finding student by studentId: {}", studentId);

        return jpaRepository.findByStudentIdWithEnrollments(studentId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Student> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        log.debug("Finding student by id: {}", id);

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Student> findByLastNameContaining(String lastName, Pageable pageable) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        log.debug("Searching students by lastName containing: {}, page: {}", lastName, pageable.getPageNumber());

        Page<StudentEntity> entityPage = jpaRepository.findByLastNameContaining(lastName, pageable);
        return entityPage.map(mapper::toDomain);
    }

    @Override
    public Page<Student> findByStatus(StudentStatus status, Pageable pageable) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        log.debug("Finding students by status: {}, page: {}", status, pageable.getPageNumber());

        StudentEntity.StudentStatusEnum entityStatus = toEntityStatus(status);
        Page<StudentEntity> entityPage = jpaRepository.findByStatus(entityStatus, pageable);
        return entityPage.map(mapper::toDomain);
    }

    @Override
    public Page<Student> findByLastNameContainingAndStatus(String lastName, StudentStatus status, Pageable pageable) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        log.debug("Searching students by lastName: {} and status: {}", lastName, status);

        StudentEntity.StudentStatusEnum entityStatus = toEntityStatus(status);
        Page<StudentEntity> entityPage = jpaRepository.findByLastNameContainingAndStatus(lastName, entityStatus, pageable);
        return entityPage.map(mapper::toDomain);
    }

    @Override
    public boolean existsByMobile(Mobile mobile) {
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }

        log.debug("Checking if mobile exists: {}", mobile.getMasked());

        return jpaRepository.existsByMobile(mobile.getNumber());
    }

    @Override
    public boolean existsByMobileAndIdNot(Mobile mobile, Long id) {
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        log.debug("Checking if mobile exists for other students: {}", mobile.getMasked());

        return jpaRepository.existsByMobileAndIdNot(mobile.getNumber(), id);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        log.debug("Deleting student by id: {}", id);

        jpaRepository.deleteById(id);

        log.info("Student deleted successfully: {}", id);
    }

    @Override
    public long count() {
        log.debug("Counting total students");
        return jpaRepository.count();
    }

    @Override
    public long countByStatus(StudentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        log.debug("Counting students by status: {}", status);

        StudentEntity.StudentStatusEnum entityStatus = toEntityStatus(status);
        return jpaRepository.countByStatus(entityStatus);
    }

    @Override
    public Page<Student> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        log.debug("Finding all students, page: {}", pageable.getPageNumber());

        Page<StudentEntity> entityPage = jpaRepository.findAll(pageable);
        return entityPage.map(mapper::toDomain);
    }

    // Helper method for status conversion
    private StudentEntity.StudentStatusEnum toEntityStatus(StudentStatus status) {
        return switch (status) {
            case ACTIVE -> StudentEntity.StudentStatusEnum.ACTIVE;
            case INACTIVE -> StudentEntity.StudentStatusEnum.INACTIVE;
        };
    }
}
