package com.school.sms.student.application.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for student response.
 * Includes all student fields and calculated age.
 */
@Data
public class StudentResponse {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer currentAge;
    private String mobile;
    private String address;
    private String fatherName;
    private String motherName;
    private String identificationMark;
    private String aadhaarNumber;
    private String email;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long version;
}
