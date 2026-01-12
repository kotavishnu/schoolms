package com.schoolms.student.presentation.dto.response;

import java.util.List;

/**
 * Student List Response DTO (BE-019)
 */
public record StudentListResponse(
    List<StudentResponse> students,
    long totalCount,
    long activeCount,
    long inactiveCount
) {
}
