package com.school.sms.configuration.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity for Configuration persistence.
 *
 * <p>Maps to the 'configuration_settings' table in the database.
 * This is separate from the domain model to maintain clean architecture.</p>
 *
 * <p>The table schema enforces a unique constraint on (category, config_key)
 * to ensure no duplicate configuration settings exist.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Entity
@Table(name = "configuration_settings",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_config_key", columnNames = {"category", "config_key"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @Column(name = "category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "data_type", length = 20)
    @Enumerated(EnumType.STRING)
    private DataTypeEnum dataType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Enum for configuration category in JPA entity.
     */
    public enum CategoryEnum {
        GENERAL,
        ACADEMIC,
        FINANCIAL
    }

    /**
     * Enum for data type in JPA entity.
     */
    public enum DataTypeEnum {
        STRING,
        NUMBER,
        BOOLEAN,
        JSON
    }

    /**
     * JPA lifecycle callback to set default values on persist.
     */
    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.version == null) {
            this.version = 0L;
        }
        if (this.isEncrypted == null) {
            this.isEncrypted = false;
        }
        if (this.dataType == null) {
            this.dataType = DataTypeEnum.STRING;
        }
    }

    /**
     * JPA lifecycle callback to update modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
