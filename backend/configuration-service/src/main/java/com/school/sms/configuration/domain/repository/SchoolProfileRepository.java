package com.school.sms.configuration.domain.repository;

import com.school.sms.configuration.domain.entity.SchoolProfile;

import java.util.Optional;

/**
 * Domain repository interface for SchoolProfile.
 * This is implemented by the infrastructure layer.
 */
public interface SchoolProfileRepository {

    /**
     * Save or update the school profile.
     * Only one profile should exist in the system.
     *
     * @param profile the school profile to save
     * @return the saved profile
     */
    SchoolProfile save(SchoolProfile profile);

    /**
     * Find the school profile.
     * Since only one record exists, this returns that record.
     *
     * @return optional containing the school profile
     */
    Optional<SchoolProfile> findProfile();

    /**
     * Find school profile by ID.
     *
     * @param id the profile ID (should always be 1)
     * @return optional containing the profile
     */
    Optional<SchoolProfile> findById(Long id);

    /**
     * Check if school profile exists.
     *
     * @return true if exists, false otherwise
     */
    boolean exists();
}
