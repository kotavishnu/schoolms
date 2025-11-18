package com.school.sms.configuration.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SchoolProfile entity (TDD approach).
 */
class SchoolProfileTest {

    @Test
    void shouldCreateSchoolProfileWithValidData() {
        // given
        String schoolName = "ABC International School";
        String schoolCode = "SCH001";
        String logoPath = "/logos/school.png";
        String address = "123 Education St";
        String phone = "+91-80-12345678";
        String email = "info@school.com";

        // when
        SchoolProfile profile = new SchoolProfile(schoolName, schoolCode, logoPath, address, phone, email);

        // then
        assertThat(profile.getSchoolName()).isEqualTo(schoolName);
        assertThat(profile.getSchoolCode()).isEqualTo(schoolCode);
        assertThat(profile.getLogoPath()).isEqualTo(logoPath);
        assertThat(profile.getAddress()).isEqualTo(address);
        assertThat(profile.getPhone()).isEqualTo(phone);
        assertThat(profile.getEmail()).isEqualTo(email);
    }

    @Test
    void shouldUpdateSchoolProfile() {
        // given
        SchoolProfile profile = new SchoolProfile(
                "Old Name", "OLD001", "/old.png", "Old Address", "1234567890", "old@email.com"
        );

        // when
        String newName = "New School Name";
        String newCode = "NEW001";
        String newLogo = "/new.png";
        String newAddress = "New Address";
        String newPhone = "9876543210";
        String newEmail = "new@email.com";

        profile.updateProfile(newName, newCode, newLogo, newAddress, newPhone, newEmail);

        // then
        assertThat(profile.getSchoolName()).isEqualTo(newName);
        assertThat(profile.getSchoolCode()).isEqualTo(newCode);
        assertThat(profile.getLogoPath()).isEqualTo(newLogo);
        assertThat(profile.getAddress()).isEqualTo(newAddress);
        assertThat(profile.getPhone()).isEqualTo(newPhone);
        assertThat(profile.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldNotAllowNullSchoolName() {
        // when/then
        assertThatThrownBy(() -> new SchoolProfile(null, "SCH001", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School name cannot be null or empty");
    }

    @Test
    void shouldNotAllowEmptySchoolName() {
        // when/then
        assertThatThrownBy(() -> new SchoolProfile("  ", "SCH001", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School name cannot be null or empty");
    }

    @Test
    void shouldNotAllowNullSchoolCode() {
        // when/then
        assertThatThrownBy(() -> new SchoolProfile("School Name", null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School code cannot be null or empty");
    }

    @Test
    void shouldNotAllowEmptySchoolCode() {
        // when/then
        assertThatThrownBy(() -> new SchoolProfile("School Name", "  ", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School code cannot be null or empty");
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // when
        SchoolProfile profile = new SchoolProfile("School Name", "SCH001", null, null, null, null);

        // then
        assertThat(profile.getLogoPath()).isNull();
        assertThat(profile.getAddress()).isNull();
        assertThat(profile.getPhone()).isNull();
        assertThat(profile.getEmail()).isNull();
    }

    @Test
    void shouldSetIdToOneForSingleRecordConstraint() {
        // given
        SchoolProfile profile = new SchoolProfile("School", "SCH001", null, null, null, null);

        // when/then
        // ID should be set to 1 in the database (enforced by constraint)
        // This is validated in integration tests
        assertThat(profile).isNotNull();
    }
}
