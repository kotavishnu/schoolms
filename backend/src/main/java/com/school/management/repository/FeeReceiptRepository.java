package com.school.management.repository;

import com.school.management.model.FeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {

    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);

    List<FeeReceipt> findByStudentId(Long studentId);

    List<FeeReceipt> findByStudentIdOrderByPaymentDateDesc(Long studentId);

    List<FeeReceipt> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    Long countByStudentId(Long studentId);
}
