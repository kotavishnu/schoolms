package com.school.sms.student.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for student summary (list view).
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryResponse {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String status;
    private LocalDateTime createdAt;
}
