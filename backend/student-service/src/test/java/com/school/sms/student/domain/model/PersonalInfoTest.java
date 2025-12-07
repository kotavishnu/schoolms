package com.school.sms.student.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PersonalInfo Tests")
class PersonalInfoTest {

    @Test
    @DisplayName("Create valid PersonalInfo")
    void constructor_WithValidData_ShouldSucceed() {
        PersonalInfo info = new PersonalInfo("John", "Doe", LocalDate.now().minusYears(10), "Mole", "123456789012");

        assertThat(info.getFirstName()).isEqualTo("John");
        assertThat(info.getLastName()).isEqualTo("Doe");
        assertThat(info.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Reject null first name")
    void constructor_WithNullFirstName_ShouldThrowException() {
        assertThatThrownBy(() -> new PersonalInfo(null, "Doe", LocalDate.now(), "Mole", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Reject blank first name")
    void constructor_WithBlankFirstName_ShouldThrowException() {
        assertThatThrownBy(() -> new PersonalInfo("  ", "Doe", LocalDate.now(), "Mole", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Reject null last name")
    void constructor_WithNullLastName_ShouldThrowException() {
        assertThatThrownBy(() -> new PersonalInfo("John", null, LocalDate.now(), "Mole", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Reject null date of birth")
    void constructor_WithNullDOB_ShouldThrowException() {
        assertThatThrownBy(() -> new PersonalInfo("John", "Doe", null, "Mole", null))
            .isInstanceOf(NullPointerException.class);
    }
}
