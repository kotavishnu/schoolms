package com.school.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeJournalDTO {

    private Long id;

    private Long studentId;

    private Long feeReceiptId;

    private LocalDate paymentDate;

    private BigDecimal amount;

    private String monthPaid;

    private Boolean isPending;

    private String academicYear;

    // Read-only fields for response
    private String studentName;
    private String receiptNumber;
}
