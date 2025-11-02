package com.school.management.dto.response;

import com.school.management.model.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for fee master response data.
 *
 * Includes computed properties and applicability status.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeMasterResponseDTO {

    private Long id;
    private FeeType feeType;
    private String feeTypeName;
    private BigDecimal amount;
    private String frequency;
    private LocalDate applicableFrom;
    private LocalDate applicableTo;
    private String description;
    private Boolean isActive;
    private String academicYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Boolean isCurrentlyApplicable;
}
