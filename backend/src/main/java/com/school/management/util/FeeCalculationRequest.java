package com.school.management.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationRequest {

    private Long studentId;
    private Integer classNumber;
    private String academicYear;
    private Boolean isFirstMonth;
    private FeeCalculationResult result;

    public FeeCalculationRequest(Long studentId, Integer classNumber, String academicYear, Boolean isFirstMonth) {
        this.studentId = studentId;
        this.classNumber = classNumber;
        this.academicYear = academicYear;
        this.isFirstMonth = isFirstMonth;
        this.result = new FeeCalculationResult();
    }
}
