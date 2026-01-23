package com.school.student.dto.response;

import com.school.student.domain.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Enrollment Response DTO
 *
 * Returned from enrollment API endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private String studentId;
    private String academicYear;
    private String gradeClass;
    private String section;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private EnrollmentStatus status;
    private String remarks;
    private Integer version;
    private LocalDateTime createdAt;
}
