package com.school.management.dto.request;

import com.school.management.model.FeeType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for fee master creation and update requests.
 *
 * Contains validation rules for fee structure configuration.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeMasterRequestDTO {

    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Amount must not exceed 999999.99")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "Frequency is required")
    @Pattern(regexp = "^(MONTHLY|QUARTERLY|ANNUAL|ONE_TIME)$",
            message = "Frequency must be MONTHLY, QUARTERLY, ANNUAL, or ONE_TIME")
    @Builder.Default
    private String frequency = "MONTHLY";

    @NotNull(message = "Applicable from date is required")
    @PastOrPresent(message = "Applicable from date cannot be in the future")
    private LocalDate applicableFrom;

    @Future(message = "Applicable to date must be in the future")
    private LocalDate applicableTo;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in format YYYY-YYYY")
    private String academicYear;
}
