package com.school.management.config;

import com.school.management.model.SchoolClass;
import com.school.management.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Data initialization component that runs on application startup.
 *
 * Seeds the database with initial data:
 * - Classes 1-10 for current academic year
 * - Default school configuration
 *
 * @author School Management Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClassRepository classRepository;

    @Override
    public void run(String... args) {
        initializeClasses();
    }

    /**
     * Initialize Classes 1-10 if database is empty
     */
    private void initializeClasses() {
        if (classRepository.count() == 0) {
            String academicYear = getCurrentAcademicYear();
            log.info("Initializing Classes 1-10 for academic year {}", academicYear);

            for (int i = 1; i <= 10; i++) {
                SchoolClass schoolClass = SchoolClass.builder()
                        .classNumber(i)
                        .section("A")
                        .academicYear(academicYear)
                        .capacity(50)
                        .currentEnrollment(0)
                        .build();

                classRepository.save(schoolClass);
                log.debug("Created Class {} - Section A", i);
            }

            log.info("Successfully initialized {} classes for academic year {}",
                     classRepository.count(), academicYear);
        } else {
            log.info("Classes already initialized. Skipping data seeding.");
        }
    }

    /**
     * Calculate current academic year based on date.
     * Academic year runs from April to March.
     *
     * @return Academic year in format "YYYY-YYYY"
     */
    private String getCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        // If current month is April or later, academic year is current-next
        // Otherwise, it's previous-current
        if (today.getMonthValue() >= 4) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
}
