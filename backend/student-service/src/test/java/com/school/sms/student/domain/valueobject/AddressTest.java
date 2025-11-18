package com.school.sms.student.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Address value object.
 */
class AddressTest {

    @Test
    @DisplayName("Should generate full address correctly")
    void shouldGenerateFullAddress() {
        // Given
        Address address = Address.builder()
            .street("123 MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pinCode("560001")
            .country("India")
            .build();

        // When
        String fullAddress = address.getFullAddress();

        // Then
        assertThat(fullAddress)
            .isEqualTo("123 MG Road, Bangalore, Karnataka - 560001, India");
    }

    @Test
    @DisplayName("Should default country to India")
    void shouldDefaultCountryToIndia() {
        // Given & When
        Address address = Address.builder()
            .street("123 MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pinCode("560001")
            .build();

        // Then
        assertThat(address.getCountry()).isEqualTo("India");
    }

    @Test
    @DisplayName("Should be equal when all fields match")
    void shouldBeEqualWhenAllFieldsMatch() {
        // Given
        Address address1 = Address.builder()
            .street("123 MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pinCode("560001")
            .country("India")
            .build();

        Address address2 = Address.builder()
            .street("123 MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pinCode("560001")
            .country("India")
            .build();

        // When & Then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }
}
