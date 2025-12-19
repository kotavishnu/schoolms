package com.school.management.dto;

import com.school.management.model.FeeFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolConfigDTO {

    private Long id;

    @NotBlank(message = "School name is required")
    private String name;

    @NotBlank(message = "School address is required")
    private String address;

    @NotNull(message = "Fee frequency is required")
    private FeeFrequency feeFrequency;
}
