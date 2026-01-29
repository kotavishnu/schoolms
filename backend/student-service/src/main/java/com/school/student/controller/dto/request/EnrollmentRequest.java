package com.school.student.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating enrollment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    @NotBlank(message = "Academic year is required")
    @Size(max = 10, message = "Academic year must not exceed 10 characters")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in format YYYY-YYYY")
    private String academicYear;

    @NotBlank(message = "Grade/Class is required")
    @Size(max = 10, message = "Grade/Class must not exceed 10 characters")
    private String gradeClass;

    @NotBlank(message = "Section is required")
    @Size(max = 5, message = "Section must not exceed 5 characters")
    private String section;

    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
