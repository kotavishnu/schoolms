package com.school.management.dto.response;

import com.school.management.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for fee journal response data.
 *
 * Includes computed properties and student details.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeJournalResponseDTO {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentMobile;
    private String className;
    private String month;
    private Integer year;
    private String monthYearDisplay;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private PaymentStatus status;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Long receiptId;
    private String receiptNumber;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Boolean isOverdue;
}
