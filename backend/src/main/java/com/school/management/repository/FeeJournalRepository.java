package com.school.management.repository;

import com.school.management.model.FeeJournal;
import com.school.management.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FeeJournal entity.
 *
 * Provides CRUD operations and custom queries for fee journal tracking.
 *
 * @author School Management Team
 */
@Repository
public interface FeeJournalRepository extends JpaRepository<FeeJournal, Long> {

    /**
     * Find journal entries by student ID
     * @param studentId Student identifier
     * @return List of journal entries
     */
    List<FeeJournal> findByStudentId(Long studentId);

    /**
     * Find journal entry by student, month, and year
     * @param studentId Student identifier
     * @param month Month name
     * @param year Year
     * @return Optional journal entry
     */
    Optional<FeeJournal> findByStudentIdAndMonthAndYear(Long studentId, String month, Integer year);

    /**
     * Find journal entries by student and status
     * @param studentId Student identifier
     * @param status Payment status
     * @return List of journal entries
     */
    List<FeeJournal> findByStudentIdAndStatus(Long studentId, PaymentStatus status);

    /**
     * Find pending journal entries for student
     * @param studentId Student identifier
     * @return List of pending entries
     */
    @Query("SELECT fj FROM FeeJournal fj WHERE fj.student.id = :studentId " +
           "AND fj.status IN ('PENDING', 'PARTIAL', 'OVERDUE') " +
           "ORDER BY fj.year, " +
           "CASE fj.month " +
           "WHEN 'January' THEN 1 " +
           "WHEN 'February' THEN 2 " +
           "WHEN 'March' THEN 3 " +
           "WHEN 'April' THEN 4 " +
           "WHEN 'May' THEN 5 " +
           "WHEN 'June' THEN 6 " +
           "WHEN 'July' THEN 7 " +
           "WHEN 'August' THEN 8 " +
           "WHEN 'September' THEN 9 " +
           "WHEN 'October' THEN 10 " +
           "WHEN 'November' THEN 11 " +
           "WHEN 'December' THEN 12 " +
           "END")
    List<FeeJournal> findPendingEntriesForStudent(@Param("studentId") Long studentId);

    /**
     * Find journal entries by year
     * @param year Year
     * @return List of journal entries
     */
    List<FeeJournal> findByYear(Integer year);

    /**
     * Find journal entries by month and year
     * @param month Month name
     * @param year Year
     * @return List of journal entries
     */
    List<FeeJournal> findByMonthAndYear(String month, Integer year);

    /**
     * Find journal entries by status
     * @param status Payment status
     * @return List of journal entries
     */
    List<FeeJournal> findByStatus(PaymentStatus status);

    /**
     * Get total pending dues for student
     * @param studentId Student identifier
     * @return Total pending amount
     */
    @Query("SELECT SUM(fj.balance) FROM FeeJournal fj " +
           "WHERE fj.student.id = :studentId " +
           "AND fj.status IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    BigDecimal getTotalPendingDues(@Param("studentId") Long studentId);

    /**
     * Get total paid amount for student
     * @param studentId Student identifier
     * @return Total paid amount
     */
    @Query("SELECT SUM(fj.amountPaid) FROM FeeJournal fj WHERE fj.student.id = :studentId")
    BigDecimal getTotalPaidAmount(@Param("studentId") Long studentId);

    /**
     * Count pending entries for student
     * @param studentId Student identifier
     * @return Count of pending entries
     */
    @Query("SELECT COUNT(fj) FROM FeeJournal fj WHERE fj.student.id = :studentId " +
           "AND fj.status IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    long countPendingEntries(@Param("studentId") Long studentId);

    /**
     * Find overdue entries
     * @return List of overdue journal entries
     */
    List<FeeJournal> findByStatusOrderByDueDateAsc(PaymentStatus status);

    /**
     * Check if journal entry exists for student, month, and year
     * @param studentId Student identifier
     * @param month Month name
     * @param year Year
     * @return true if exists
     */
    boolean existsByStudentIdAndMonthAndYear(Long studentId, String month, Integer year);
}
