package com.school.sms.configuration.domain.entity;

import com.school.sms.configuration.domain.enums.SettingCategory;
import com.school.sms.configuration.domain.exception.InvalidSettingKeyException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Domain entity representing a configuration setting.
 * Settings are categorized and stored as key-value pairs.
 */
@Entity
@Table(name = "configuration_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category", "key"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class ConfigurationSetting {

    private static final Pattern KEY_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettingCategory category;

    @Column(nullable = false, length = 100)
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(length = 500)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy = "SYSTEM";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "SYSTEM";

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Constructor for creating a new configuration setting.
     *
     * @param category    the setting category
     * @param key         the setting key in UPPERCASE_SNAKE_CASE format
     * @param value       the setting value
     * @param description optional description
     * @throws InvalidSettingKeyException if key is not in valid format
     */
    public ConfigurationSetting(SettingCategory category, String key, String value, String description) {
        validateKey(key);
        validateValue(value);
        this.category = category;
        this.key = key;
        this.value = value;
        this.description = description;
    }

    /**
     * Update the setting value.
     *
     * @param newValue the new value
     * @throws IllegalArgumentException if value is null or empty
     */
    public void updateValue(String newValue) {
        validateValue(newValue);
        this.value = newValue;
    }

    /**
     * Update the setting description.
     *
     * @param newDescription the new description
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Set the user who is updating this setting.
     *
     * @param userId the user ID
     */
    public void setUpdatedBy(String userId) {
        this.updatedBy = userId;
    }

    /**
     * Set the user who created this setting.
     *
     * @param userId the user ID
     */
    public void setCreatedBy(String userId) {
        this.createdBy = userId;
    }

    /**
     * Validate that the key is in UPPERCASE_SNAKE_CASE format.
     *
     * @param key the key to validate
     * @throws InvalidSettingKeyException if key is invalid
     */
    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new InvalidSettingKeyException("Key cannot be null or empty");
        }
        if (!KEY_PATTERN.matcher(key).matches()) {
            throw new InvalidSettingKeyException(key);
        }
    }

    /**
     * Validate that the value is not null or empty.
     *
     * @param value the value to validate
     * @throws IllegalArgumentException if value is invalid
     */
    private void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
    }
}
