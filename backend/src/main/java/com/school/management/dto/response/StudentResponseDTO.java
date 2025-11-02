package com.school.management.dto.response;

import com.school.management.model.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for student response data.
 *
 * Includes computed fields like fullName, age, and rollNumber.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String caste;
    private String mobile;
    private String religion;
    private String molesOnBody;
    private String motherName;
    private String fatherName;
    private Long classId;
    private String className;
    private Integer classNumber;
    private String section;
    private String rollNumber;
    private LocalDate enrollmentDate;
    private StudentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
