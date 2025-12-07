package com.school.sms.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ConfigurationSetting Domain Model Tests")
class ConfigurationSettingTest {

    @Test
    @DisplayName("Create new configuration setting")
    void createNew_WithValidData_ShouldSucceed() {
        ConfigurationKey key = ConfigurationKey.of("school_name");
        ConfigurationValue value = ConfigurationValue.of("ABC School");

        ConfigurationSetting setting = ConfigurationSetting.createNew(
            Category.GENERAL,
            key,
            value,
            DataType.STRING,
            "School name configuration",
            false,
            "admin"
        );

        assertThat(setting).isNotNull();
        assertThat(setting.getCategory()).isEqualTo(Category.GENERAL);
        assertThat(setting.getConfigKey()).isEqualTo(key);
        assertThat(setting.getConfigValue()).isEqualTo(value);
        assertThat(setting.getDataType()).isEqualTo(DataType.STRING);
        assertThat(setting.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Default data type to STRING if null")
    void createNew_WithNullDataType_ShouldDefaultToString() {
        ConfigurationKey key = ConfigurationKey.of("school_code");
        ConfigurationValue value = ConfigurationValue.of("ABC123");

        ConfigurationSetting setting = ConfigurationSetting.createNew(
            Category.GENERAL,
            key,
            value,
            null,
            "School code",
            false,
            "admin"
        );

        assertThat(setting.getDataType()).isEqualTo(DataType.STRING);
    }

    @Test
    @DisplayName("Create encrypted configuration")
    void createNew_WithEncryption_ShouldSetEncryptedFlag() {
        ConfigurationKey key = ConfigurationKey.of("api_key");
        ConfigurationValue value = ConfigurationValue.of("secret123");

        ConfigurationSetting setting = ConfigurationSetting.createNew(
            Category.GENERAL,
            key,
            value,
            DataType.STRING,
            "API Key",
            true,
            "admin"
        );

        assertThat(setting.isEncrypted()).isTrue();
    }

    @Test
    @DisplayName("FromRepository should recreate configuration")
    void fromRepository_WithValidData_ShouldRecreateConfiguration() {
        ConfigurationKey key = ConfigurationKey.of("school_name");
        ConfigurationValue value = ConfigurationValue.of("ABC School");

        ConfigurationSetting setting = ConfigurationSetting.fromRepository(
            1L,
            Category.GENERAL,
            key,
            value,
            DataType.STRING,
            "Description",
            false,
            0L,
            null,
            "admin"
        );

        assertThat(setting).isNotNull();
        assertThat(setting.getSettingId()).isEqualTo(1L);
        assertThat(setting.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Update configuration value")
    void updateValue_WithNewValue_ShouldReturnUpdatedConfiguration() {
        ConfigurationKey key = ConfigurationKey.of("school_name");
        ConfigurationValue oldValue = ConfigurationValue.of("ABC School");
        ConfigurationValue newValue = ConfigurationValue.of("XYZ School");

        ConfigurationSetting original = ConfigurationSetting.createNew(
            Category.GENERAL,
            key,
            oldValue,
            DataType.STRING,
            "School name",
            false,
            "admin"
        );

        ConfigurationSetting updated = original.updateValue(newValue, null, "Updated school name", "admin");

        assertThat(updated.getConfigValue()).isEqualTo(newValue);
        assertThat(updated.getUpdatedBy()).isEqualTo("admin");
    }
}
