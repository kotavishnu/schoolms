package com.school.sms.student.application.dto;

import lombok.Data;

/**
 * DTO for student search criteria.
 */
@Data
public class StudentSearchCriteria {

    private String lastName;
    private String guardianName;
    private String status;
}
