package com.school.sms.student.presentation.dto;

import com.school.sms.student.application.dto.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated student queries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPageResponse {

    private List<StudentDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
