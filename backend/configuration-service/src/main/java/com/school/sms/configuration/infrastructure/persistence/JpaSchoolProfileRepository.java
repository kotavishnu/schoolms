package com.school.sms.configuration.infrastructure.persistence;

import com.school.sms.configuration.domain.entity.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for SchoolProfile entity.
 */
@Repository
public interface JpaSchoolProfileRepository extends JpaRepository<SchoolProfile, Long> {

    /**
     * Find the school profile.
     * Since only one record exists (id = 1), this returns that record.
     *
     * @return optional containing the profile
     */
    @Query("SELECT sp FROM SchoolProfile sp WHERE sp.id = 1")
    Optional<SchoolProfile> findProfile();
}
