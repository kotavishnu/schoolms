package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.SchoolProfileResponse;
import com.school.sms.configuration.application.dto.UpdateSchoolProfileRequest;
import com.school.sms.configuration.application.mapper.ConfigurationMapper;
import com.school.sms.configuration.domain.entity.SchoolProfile;
import com.school.sms.configuration.domain.exception.SchoolProfileNotFoundException;
import com.school.sms.configuration.domain.repository.SchoolProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing school profile.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchoolProfileService {

    private final SchoolProfileRepository repository;
    private final ConfigurationMapper mapper;

    /**
     * Get the school profile.
     *
     * @return the school profile
     * @throws SchoolProfileNotFoundException if profile not found
     */
    @Transactional(readOnly = true)
    public SchoolProfileResponse getSchoolProfile() {
        log.debug("Fetching school profile");

        SchoolProfile profile = repository.findProfile()
                .orElseThrow(SchoolProfileNotFoundException::new);

        return mapper.toSchoolProfileResponse(profile);
    }

    /**
     * Update the school profile.
     *
     * @param request the update request
     * @param userId  the user updating the profile
     * @return the updated profile
     * @throws SchoolProfileNotFoundException if profile not found
     */
    public SchoolProfileResponse updateSchoolProfile(UpdateSchoolProfileRequest request, String userId) {
        log.info("Updating school profile");

        SchoolProfile profile = repository.findProfile()
                .orElseThrow(SchoolProfileNotFoundException::new);

        // Update profile
        profile.updateProfile(
                request.getSchoolName(),
                request.getSchoolCode(),
                request.getLogoPath(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail()
        );
        profile.setUpdatedBy(userId);

        SchoolProfile updatedProfile = repository.save(profile);
        log.info("School profile updated successfully");

        return mapper.toSchoolProfileResponse(updatedProfile);
    }
}
