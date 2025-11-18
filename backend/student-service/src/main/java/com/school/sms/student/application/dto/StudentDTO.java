package com.school.sms.student.application.dto;

import com.school.sms.student.domain.valueobject.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Application DTO for Student.
 * Used for transferring student data between application layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer age;

    // Address
    private AddressDTO address;

    // Contact
    private String mobile;
    private String email;

    // Family
    private String fatherNameOrGuardian;
    private String motherName;

    // Additional
    private String caste;
    private String moles;
    private String aadhaarNumber;

    // Status
    private StudentStatus status;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
