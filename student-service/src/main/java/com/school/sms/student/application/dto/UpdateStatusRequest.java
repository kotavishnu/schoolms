package com.school.sms.student.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating student status.
 */
@Data
public class UpdateStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Version is required for optimistic locking")
    private Long version;
}
