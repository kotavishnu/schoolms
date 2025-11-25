package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Spring Data JPA repository for StudentJpaEntity.
 */
@Repository
public interface JpaStudentRepository extends JpaRepository<StudentJpaEntity, Long> {

    Optional<StudentJpaEntity> findByStudentId(String studentId);

    Optional<StudentJpaEntity> findByMobile(String mobile);

    Page<StudentJpaEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<StudentJpaEntity> findByFatherNameContainingIgnoreCase(String fatherName, Pageable pageable);

    Page<StudentJpaEntity> findByStatus(String status, Pageable pageable);

    boolean existsByMobile(String mobile);

    @Query("SELECT COUNT(s) FROM StudentJpaEntity s WHERE s.studentId LIKE CONCAT(:prefix, '%')")
    long countByStudentIdStartingWith(@Param("prefix") String prefix);
}
