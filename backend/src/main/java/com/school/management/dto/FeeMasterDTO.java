package com.school.management.dto;

import com.school.management.model.FeeFrequency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeMasterDTO {

    private Long id;

    @NotBlank(message = "Fee type is required")
    private String feeType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Applicable class from is required")
    @Min(value = 1, message = "Class must be between 1 and 10")
    @Max(value = 10, message = "Class must be between 1 and 10")
    private Integer applicableClassFrom;

    @NotNull(message = "Applicable class to is required")
    @Min(value = 1, message = "Class must be between 1 and 10")
    @Max(value = 10, message = "Class must be between 1 and 10")
    private Integer applicableClassTo;

    @NotNull(message = "Frequency is required")
    private FeeFrequency frequency;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    private Boolean isActive;
}
