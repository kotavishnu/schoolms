package com.schoolms.student.presentation.dto.response;

/**
 * Student Statistics Response DTO (BE-019)
 */
public record StudentStatisticsResponse(
    long totalStudents,
    long activeStudents,
    long inactiveStudents
) {
}
