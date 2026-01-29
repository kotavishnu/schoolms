package com.school.student.controller.dto.response;

import com.school.student.domain.model.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for student data
 * Includes computed field: age
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer age; // Computed field
    private String mobile;
    private String email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private String aadhaarNumber;
    private StudentStatus status;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
