package com.school.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequestDTO {

    @NotNull(message = "Class number is required")
    @Min(value = 1, message = "Class number must be between 1 and 10")
    @Max(value = 10, message = "Class number must be between 1 and 10")
    private Integer classNumber;

    @NotBlank(message = "Section is required")
    @Size(max = 10, message = "Section must not exceed 10 characters")
    private String section;

    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in format YYYY-YYYY")
    private String academicYear;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity must not exceed 100")
    private Integer capacity;

    @Size(max = 100, message = "Class teacher name must not exceed 100 characters")
    private String classTeacher;

    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;
}
