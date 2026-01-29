package com.school.student.controller.dto.response;

import com.school.student.domain.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for enrollment data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private Long studentId;
    private String academicYear;
    private String gradeClass;
    private String section;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private EnrollmentStatus status;
    private String remarks;
    private LocalDateTime createdAt;
}
