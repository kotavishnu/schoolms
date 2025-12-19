package com.school.management.config;

import com.school.management.model.SchoolClass;
import com.school.management.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SchoolClassRepository classRepository;

    @Override
    public void run(String... args) {
        initializeClasses();
    }

    private void initializeClasses() {
        String currentAcademicYear = getCurrentAcademicYear();

        // Check if classes already exist for current academic year
        long existingClasses = classRepository.findByAcademicYear(currentAcademicYear).size();

        if (existingClasses > 0) {
            log.info("Classes already initialized for academic year: {}", currentAcademicYear);
            return;
        }

        log.info("Initializing Classes 1-10 for academic year: {}", currentAcademicYear);

        // Create Classes 1-10 with Section A
        for (int i = 1; i <= 10; i++) {
            SchoolClass schoolClass = SchoolClass.builder()
                    .classNumber(i)
                    .section("A")
                    .academicYear(currentAcademicYear)
                    .capacity(40)
                    .currentStrength(0)
                    .build();

            classRepository.save(schoolClass);
            log.info("Created Class {} Section A", i);
        }

        log.info("Successfully initialized {} classes", 10);
    }

    private String getCurrentAcademicYear() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int month = now.getMonthValue();

        // If month is April or later, academic year is current-next
        // Otherwise, it's previous-current
        if (month >= 4) {
            return currentYear + "-" + (currentYear + 1);
        } else {
            return (currentYear - 1) + "-" + currentYear;
        }
    }
}
