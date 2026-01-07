package com.school.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class SchoolClassDTO {

    private Long id;

    @NotNull(message = "Class number is required")
    @Min(value = 1, message = "Class number must be between 1 and 10")
    @Max(value = 10, message = "Class number must be between 1 and 10")
    private Integer classNumber;

    private String section;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Integer currentStrength;
}
