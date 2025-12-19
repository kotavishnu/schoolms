package com.school.management.repository;

import com.school.management.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    List<SchoolClass> findByAcademicYear(String academicYear);

    Optional<SchoolClass> findByClassNumberAndSectionAndAcademicYear(
        Integer classNumber, String section, String academicYear);

    List<SchoolClass> findByClassNumber(Integer classNumber);
}
