package com.school.management.repository;

import com.school.management.model.SchoolConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolConfigRepository extends JpaRepository<SchoolConfig, Long> {

    Optional<SchoolConfig> findFirstByOrderByIdDesc();
}
