package com.school.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for school configuration response data.
 *
 * Includes computed properties and metadata.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolConfigResponseDTO {

    private Long id;
    private String configKey;
    private String configValue;
    private String category;
    private String description;
    private Boolean isEditable;
    private String dataType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Integer intValue;
    private Boolean booleanValue;
    private Boolean isSystemConfig;
}
