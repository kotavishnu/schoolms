package com.school.config.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity for Configuration table.
 * Maps to 'configurations' table in database.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Entity
@Table(name = "configurations", uniqueConstraints = {
    @UniqueConstraint(name = "uk_category_key", columnNames = {"category", "config_key"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private com.school.config.domain.model.ConfigCategory category;

    @Column(name = "config_key", nullable = false, length = 100)
    private String key;

    @Column(name = "config_value", nullable = false, length = 1000)
    private String value;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "data_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private com.school.config.domain.model.DataType dataType;

    @Column(name = "is_encrypted", nullable = false)
    private boolean isEncrypted;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (version == null) {
            version = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
