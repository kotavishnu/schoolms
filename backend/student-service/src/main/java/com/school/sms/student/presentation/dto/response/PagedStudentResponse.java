package com.school.sms.student.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated student list.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedStudentResponse {

    private List<StudentSummaryResponse> content;
    private PageableInfo pageInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }
}
