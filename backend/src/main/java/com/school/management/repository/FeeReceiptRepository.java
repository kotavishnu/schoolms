package com.school.management.repository;

import com.school.management.model.FeeReceipt;
import com.school.management.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FeeReceipt entity.
 *
 * Provides CRUD operations and custom queries for fee receipt management.
 *
 * @author School Management Team
 */
@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {

    /**
     * Find receipt by receipt number
     * @param receiptNumber Unique receipt number
     * @return Optional receipt
     */
    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);

    /**
     * Find receipts by student ID
     * @param studentId Student identifier
     * @return List of receipts for the student
     */
    List<FeeReceipt> findByStudentId(Long studentId);

    /**
     * Find receipts by student ID ordered by date descending
     * @param studentId Student identifier
     * @return List of receipts
     */
    @Query("SELECT r FROM FeeReceipt r WHERE r.student.id = :studentId " +
           "ORDER BY r.paymentDate DESC")
    List<FeeReceipt> findByStudentIdOrderByPaymentDateDesc(@Param("studentId") Long studentId);

    /**
     * Find receipts by payment date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of receipts in date range
     */
    List<FeeReceipt> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find receipts by payment method
     * @param paymentMethod Payment method enum
     * @return List of receipts
     */
    List<FeeReceipt> findByPaymentMethod(PaymentMethod paymentMethod);

    /**
     * Check if receipt number exists
     * @param receiptNumber Receipt number to check
     * @return true if exists
     */
    boolean existsByReceiptNumber(String receiptNumber);

    /**
     * Get last receipt number for year
     * @param year Year to search
     * @return Optional receipt with highest number
     */
    @Query("SELECT r FROM FeeReceipt r WHERE r.receiptNumber LIKE CONCAT('REC-', :year, '-%') " +
           "ORDER BY r.receiptNumber DESC")
    List<FeeReceipt> findLastReceiptForYear(@Param("year") String year);

    /**
     * Get total collection for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount collected
     */
    @Query("SELECT SUM(r.amount) FROM FeeReceipt r " +
           "WHERE r.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCollection(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get total collection by payment method
     * @param paymentMethod Payment method
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount
     */
    @Query("SELECT SUM(r.amount) FROM FeeReceipt r " +
           "WHERE r.paymentMethod = :paymentMethod " +
           "AND r.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCollectionByMethod(
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find receipts generated on specific date
     * @param date Payment date
     * @return List of receipts
     */
    List<FeeReceipt> findByPaymentDate(LocalDate date);

    /**
     * Count receipts for student
     * @param studentId Student identifier
     * @return Number of receipts
     */
    long countByStudentId(Long studentId);
}
