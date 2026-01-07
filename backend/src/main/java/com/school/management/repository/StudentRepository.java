package com.school.management.repository;

import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassId(Long classId);

    List<Student> findByStatus(StudentStatus status);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchByName(@Param("query") String query);

    @Query("SELECT s FROM Student s WHERE s.classId = :classId AND s.status = :status")
    List<Student> findByClassIdAndStatus(@Param("classId") Long classId,
                                         @Param("status") StudentStatus status);
}
