package com.school.sms.configuration.domain.entity;

import com.school.sms.configuration.domain.enums.SettingCategory;
import com.school.sms.configuration.domain.exception.InvalidSettingKeyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ConfigurationSetting entity (TDD approach).
 */
class ConfigurationSettingTest {

    @Test
    void shouldCreateConfigurationSettingWithValidData() {
        // given
        SettingCategory category = SettingCategory.GENERAL;
        String key = "SCHOOL_TIMEZONE";
        String value = "Asia/Kolkata";
        String description = "Default timezone";

        // when
        ConfigurationSetting setting = new ConfigurationSetting(category, key, value, description);

        // then
        assertThat(setting.getCategory()).isEqualTo(category);
        assertThat(setting.getKey()).isEqualTo(key);
        assertThat(setting.getValue()).isEqualTo(value);
        assertThat(setting.getDescription()).isEqualTo(description);
    }

    @Test
    void shouldValidateKeyIsInUppercaseSnakeCase() {
        // given
        String validKey = "SCHOOL_TIMEZONE";
        String invalidKey1 = "schoolTimezone";  // camelCase
        String invalidKey2 = "School-Timezone"; // kebab-case
        String invalidKey3 = "school timezone"; // with space

        // when/then - valid key should not throw
        assertThatCode(() -> new ConfigurationSetting(SettingCategory.GENERAL, validKey, "value", "desc"))
                .doesNotThrowAnyException();

        // when/then - invalid keys should throw
        assertThatThrownBy(() -> new ConfigurationSetting(SettingCategory.GENERAL, invalidKey1, "value", "desc"))
                .isInstanceOf(InvalidSettingKeyException.class);

        assertThatThrownBy(() -> new ConfigurationSetting(SettingCategory.GENERAL, invalidKey2, "value", "desc"))
                .isInstanceOf(InvalidSettingKeyException.class);

        assertThatThrownBy(() -> new ConfigurationSetting(SettingCategory.GENERAL, invalidKey3, "value", "desc"))
                .isInstanceOf(InvalidSettingKeyException.class);
    }

    @Test
    void shouldUpdateValue() {
        // given
        ConfigurationSetting setting = new ConfigurationSetting(
                SettingCategory.GENERAL, "TIMEZONE", "Asia/Kolkata", "Timezone"
        );

        // when
        String newValue = "Asia/Calcutta";
        setting.updateValue(newValue);

        // then
        assertThat(setting.getValue()).isEqualTo(newValue);
    }

    @Test
    void shouldUpdateDescription() {
        // given
        ConfigurationSetting setting = new ConfigurationSetting(
                SettingCategory.GENERAL, "TIMEZONE", "Asia/Kolkata", "Old description"
        );

        // when
        String newDescription = "New description";
        setting.updateDescription(newDescription);

        // then
        assertThat(setting.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void shouldNotAllowNullOrEmptyValue() {
        // given
        ConfigurationSetting setting = new ConfigurationSetting(
                SettingCategory.GENERAL, "TIMEZONE", "Asia/Kolkata", "Timezone"
        );

        // when/then
        assertThatThrownBy(() -> setting.updateValue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value cannot be null or empty");

        assertThatThrownBy(() -> setting.updateValue(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value cannot be null or empty");

        assertThatThrownBy(() -> setting.updateValue("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value cannot be null or empty");
    }

    @Test
    void shouldCreateSettingWithoutDescription() {
        // given
        SettingCategory category = SettingCategory.ACADEMIC;
        String key = "MAX_STUDENTS";
        String value = "100";

        // when
        ConfigurationSetting setting = new ConfigurationSetting(category, key, value, null);

        // then
        assertThat(setting.getDescription()).isNull();
    }
}
