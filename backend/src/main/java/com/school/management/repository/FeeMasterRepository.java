package com.school.management.repository;

import com.school.management.model.FeeMaster;
import com.school.management.model.FeeFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeMasterRepository extends JpaRepository<FeeMaster, Long> {

    List<FeeMaster> findByAcademicYear(String academicYear);

    List<FeeMaster> findByAcademicYearAndIsActive(String academicYear, Boolean isActive);

    @Query("SELECT f FROM FeeMaster f WHERE " +
           "f.academicYear = :academicYear AND " +
           "f.isActive = true AND " +
           ":classNumber BETWEEN f.applicableClassFrom AND f.applicableClassTo")
    List<FeeMaster> findApplicableFeesForClass(
        @Param("academicYear") String academicYear,
        @Param("classNumber") Integer classNumber);

    List<FeeMaster> findByFeeTypeAndAcademicYear(String feeType, String academicYear);
}
