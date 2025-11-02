package com.school.management.dto.request;

import com.school.management.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeReceiptRequestDTO {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    private Long studentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;

    @Size(max = 50, message = "Cheque number must not exceed 50 characters")
    private String chequeNumber;

    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    @NotEmpty(message = "Months paid list cannot be empty")
    private List<String> monthsPaid;

    @NotNull(message = "Fee breakdown is required")
    private Map<String, Object> feeBreakdown;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
