package com.school.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Enrollment Request DTO
 *
 * Used for creating new enrollment records for a student.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    @NotBlank(message = "Grade/class is required")
    @Size(max = 50, message = "Grade/class must not exceed 50 characters")
    private String gradeClass;

    @NotBlank(message = "Section is required")
    @Size(max = 10, message = "Section must not exceed 10 characters")
    private String section;

    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
