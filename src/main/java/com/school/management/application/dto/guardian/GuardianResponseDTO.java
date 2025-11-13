package com.school.management.application.dto.guardian;

import com.school.management.domain.student.Relationship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for guardian information
 * Contains decrypted guardian data
 *
 * @author School Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianResponseDTO {

    private Long guardianId;
    private Long studentId;
    private Relationship relationship;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobile;
    private String email;
    private String occupation;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
