package com.school.sms.configuration.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for school profile response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolProfileResponse {
    private Long id;
    private String schoolName;
    private String schoolCode;
    private String logoPath;
    private String address;
    private String phone;
    private String email;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
