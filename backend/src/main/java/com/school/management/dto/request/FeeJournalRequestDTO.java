package com.school.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for fee journal creation and update requests.
 *
 * Contains validation rules for fee journal tracking.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeJournalRequestDTO {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    private Long studentId;

    @NotBlank(message = "Month is required")
    @Pattern(regexp = "^(January|February|March|April|May|June|July|August|September|October|November|December)$",
            message = "Month must be a valid month name")
    private String month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    @NotNull(message = "Amount due is required")
    @DecimalMin(value = "0.0", message = "Amount due must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Amount due must have at most 8 integer digits and 2 decimal places")
    private BigDecimal amountDue;

    @DecimalMin(value = "0.0", message = "Amount paid must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Amount paid must have at most 8 integer digits and 2 decimal places")
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
