package com.school.management.dto.response;

import com.school.management.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeReceiptResponseDTO {

    private Long id;
    private String receiptNumber;
    private Long studentId;
    private String studentName;
    private String className;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String chequeNumber;
    private String bankName;
    private String monthsPaid;
    private Map<String, Object> feeBreakdown;
    private String remarks;
    private String generatedBy;
    private String pdfUrl;
    private LocalDateTime createdAt;
}
