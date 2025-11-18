package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.SchoolProfileResponse;
import com.school.sms.configuration.application.dto.UpdateSchoolProfileRequest;
import com.school.sms.configuration.application.mapper.ConfigurationMapper;
import com.school.sms.configuration.domain.entity.SchoolProfile;
import com.school.sms.configuration.domain.exception.SchoolProfileNotFoundException;
import com.school.sms.configuration.domain.repository.SchoolProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SchoolProfileService (TDD approach).
 */
@ExtendWith(MockitoExtension.class)
class SchoolProfileServiceTest {

    @Mock
    private SchoolProfileRepository repository;

    @Mock
    private ConfigurationMapper mapper;

    @InjectMocks
    private SchoolProfileService service;

    private SchoolProfile profile;
    private SchoolProfileResponse response;

    @BeforeEach
    void setUp() {
        profile = new SchoolProfile(
                "ABC School",
                "SCH001",
                "/logo.png",
                "123 Street",
                "1234567890",
                "info@school.com"
        );

        response = SchoolProfileResponse.builder()
                .id(1L)
                .schoolName("ABC School")
                .schoolCode("SCH001")
                .logoPath("/logo.png")
                .address("123 Street")
                .phone("1234567890")
                .email("info@school.com")
                .build();
    }

    @Test
    void shouldGetSchoolProfile() {
        // given
        when(repository.findProfile()).thenReturn(Optional.of(profile));
        when(mapper.toSchoolProfileResponse(profile)).thenReturn(response);

        // when
        SchoolProfileResponse result = service.getSchoolProfile();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSchoolName()).isEqualTo("ABC School");
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        // given
        when(repository.findProfile()).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> service.getSchoolProfile())
                .isInstanceOf(SchoolProfileNotFoundException.class);
    }

    @Test
    void shouldUpdateSchoolProfile() {
        // given
        UpdateSchoolProfileRequest request = new UpdateSchoolProfileRequest(
                "New School Name",
                "NEW001",
                "/new-logo.png",
                "New Address",
                "9876543210",
                "new@school.com"
        );

        when(repository.findProfile()).thenReturn(Optional.of(profile));
        when(repository.save(any(SchoolProfile.class))).thenReturn(profile);
        when(mapper.toSchoolProfileResponse(any(SchoolProfile.class))).thenReturn(response);

        // when
        SchoolProfileResponse result = service.updateSchoolProfile(request, "USER123");

        // then
        assertThat(result).isNotNull();
        verify(repository).save(any(SchoolProfile.class));
    }
}
