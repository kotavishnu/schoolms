package com.schoolms.student.infrastructure.persistence.repository;

import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Student JPA Repository (BE-013)
 * Spring Data JPA repository for database operations
 */
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    Optional<StudentJpaEntity> findByStudentId(String studentId);

    Optional<StudentJpaEntity> findByPhone(String phone);

    Optional<StudentJpaEntity> findByEmail(String email);

    Optional<StudentJpaEntity> findByAdhaarNumber(String adhaarNumber);

    List<StudentJpaEntity> findByStatus(StudentStatus status);

    @Query("SELECT s FROM StudentJpaEntity s WHERE " +
           "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.guardianName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentJpaEntity> search(@Param("query") String query);

    long countByStatus(StudentStatus status);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByAdhaarNumber(String adhaarNumber);
}
