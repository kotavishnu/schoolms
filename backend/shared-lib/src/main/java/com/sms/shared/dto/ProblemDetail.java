package com.sms.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RFC 7807 Problem Details for HTTP APIs.
 * Standard error response format for all REST APIs.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetail {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private String correlationId;
    private LocalDateTime timestamp;
    private List<FieldError> errors;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private String field;
        private String message;
        private String rejectedValue;
        private String code;
    }
}
