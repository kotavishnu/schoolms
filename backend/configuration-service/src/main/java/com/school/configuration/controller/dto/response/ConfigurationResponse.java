package com.school.configuration.controller.dto.response;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for Configuration responses.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ConfigCategory category;
    private String key;
    private String value;
    private String description;
    private DataType dataType;
    private Boolean isEncrypted;
    private Long version;
    private LocalDateTime updatedAt;
}
