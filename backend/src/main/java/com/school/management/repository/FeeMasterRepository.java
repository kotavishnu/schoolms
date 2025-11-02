package com.school.management.repository;

import com.school.management.model.FeeMaster;
import com.school.management.model.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FeeMaster entity.
 *
 * Provides CRUD operations and custom queries for fee master configuration.
 *
 * @author School Management Team
 */
@Repository
public interface FeeMasterRepository extends JpaRepository<FeeMaster, Long> {

    /**
     * Find fee master by fee type
     * @param feeType Fee type enum
     * @return List of fee masters for the type
     */
    List<FeeMaster> findByFeeType(FeeType feeType);

    /**
     * Find active fee masters by fee type
     * @param feeType Fee type enum
     * @param isActive Active status
     * @return List of active fee masters
     */
    List<FeeMaster> findByFeeTypeAndIsActive(FeeType feeType, Boolean isActive);

    /**
     * Find fee masters by academic year
     * @param academicYear Academic year
     * @return List of fee masters for the year
     */
    List<FeeMaster> findByAcademicYear(String academicYear);

    /**
     * Find active fee masters for academic year
     * @param academicYear Academic year
     * @param isActive Active status
     * @return List of active fee masters
     */
    List<FeeMaster> findByAcademicYearAndIsActive(String academicYear, Boolean isActive);

    /**
     * Find fee master by fee type and applicable from date
     * @param feeType Fee type
     * @param applicableFrom Applicable from date
     * @return Optional fee master
     */
    Optional<FeeMaster> findByFeeTypeAndApplicableFrom(FeeType feeType, LocalDate applicableFrom);

    /**
     * Find currently applicable fee masters
     * @param currentDate Current date to check applicability
     * @return List of applicable fee masters
     */
    @Query("SELECT fm FROM FeeMaster fm WHERE fm.isActive = true " +
           "AND fm.applicableFrom <= :currentDate " +
           "AND (fm.applicableTo IS NULL OR fm.applicableTo >= :currentDate)")
    List<FeeMaster> findCurrentlyApplicable(@Param("currentDate") LocalDate currentDate);

    /**
     * Find currently applicable fee master by fee type
     * @param feeType Fee type
     * @param currentDate Current date
     * @return Optional fee master
     */
    @Query("SELECT fm FROM FeeMaster fm WHERE fm.feeType = :feeType " +
           "AND fm.isActive = true " +
           "AND fm.applicableFrom <= :currentDate " +
           "AND (fm.applicableTo IS NULL OR fm.applicableTo >= :currentDate) " +
           "ORDER BY fm.applicableFrom DESC")
    Optional<FeeMaster> findLatestApplicable(
            @Param("feeType") FeeType feeType,
            @Param("currentDate") LocalDate currentDate);

    /**
     * Find all active fee masters
     * @return List of active fee masters
     */
    List<FeeMaster> findByIsActiveTrue();

    /**
     * Count active fee masters for academic year
     * @param academicYear Academic year
     * @return Count of active fee masters
     */
    long countByAcademicYearAndIsActive(String academicYear, Boolean isActive);
}
