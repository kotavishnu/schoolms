package com.school.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDTO {

    private Long id;
    private Integer classNumber;
    private String section;
    private String academicYear;
    private Integer capacity;
    private Integer currentEnrollment;
    private Integer availableSeats;
    private String classTeacher;
    private String roomNumber;
    private String displayName;
    private Boolean hasAvailableSeats;
    private Boolean isAlmostFull;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
