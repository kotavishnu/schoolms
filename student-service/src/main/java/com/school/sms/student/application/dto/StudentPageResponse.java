package com.school.sms.student.application.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO for paginated student response.
 */
@Data
public class StudentPageResponse {

    private List<StudentResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
}
