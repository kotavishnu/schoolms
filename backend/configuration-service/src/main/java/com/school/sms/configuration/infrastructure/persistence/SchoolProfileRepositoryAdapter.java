package com.school.sms.configuration.infrastructure.persistence;

import com.school.sms.configuration.domain.entity.SchoolProfile;
import com.school.sms.configuration.domain.repository.SchoolProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementation of SchoolProfileRepository.
 * Delegates to JPA repository.
 */
@Component
@RequiredArgsConstructor
public class SchoolProfileRepositoryAdapter implements SchoolProfileRepository {

    private final JpaSchoolProfileRepository jpaRepository;

    @Override
    public SchoolProfile save(SchoolProfile profile) {
        return jpaRepository.save(profile);
    }

    @Override
    public Optional<SchoolProfile> findProfile() {
        return jpaRepository.findProfile();
    }

    @Override
    public Optional<SchoolProfile> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean exists() {
        return jpaRepository.count() > 0;
    }
}
