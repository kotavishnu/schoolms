package com.school.sms.student.application.service;

import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.valueobject.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for student statistics and aggregations.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StudentStatisticsService {

    private final StudentRepository studentRepository;

    /**
     * Gets comprehensive student statistics.
     *
     * @return map containing various statistics
     */
    public Map<String, Object> getStatistics() {
        log.debug("Calculating student statistics");

        List<Student> allStudents = studentRepository.findAll();

        Map<String, Object> statistics = new HashMap<>();

        // Total counts
        long totalStudents = allStudents.size();
        long activeStudents = allStudents.stream()
                .filter(s -> s.getStatus() == StudentStatus.ACTIVE)
                .count();
        long inactiveStudents = totalStudents - activeStudents;

        statistics.put("totalStudents", totalStudents);
        statistics.put("activeStudents", activeStudents);
        statistics.put("inactiveStudents", inactiveStudents);

        // Average age
        if (totalStudents > 0) {
            double averageAge = allStudents.stream()
                    .mapToInt(Student::calculateAge)
                    .average()
                    .orElse(0.0);
            statistics.put("averageAge", Math.round(averageAge * 10.0) / 10.0);
        } else {
            statistics.put("averageAge", 0.0);
        }

        // Age distribution
        Map<String, Long> ageDistribution = new HashMap<>();
        ageDistribution.put("3-6", allStudents.stream()
                .filter(s -> s.calculateAge() >= 3 && s.calculateAge() <= 6)
                .count());
        ageDistribution.put("7-10", allStudents.stream()
                .filter(s -> s.calculateAge() >= 7 && s.calculateAge() <= 10)
                .count());
        ageDistribution.put("11-14", allStudents.stream()
                .filter(s -> s.calculateAge() >= 11 && s.calculateAge() <= 14)
                .count());
        ageDistribution.put("15-18", allStudents.stream()
                .filter(s -> s.calculateAge() >= 15 && s.calculateAge() <= 18)
                .count());
        statistics.put("ageDistribution", ageDistribution);

        // Caste distribution
        Map<String, Long> casteDistribution = allStudents.stream()
                .filter(s -> s.getCaste() != null && !s.getCaste().isBlank())
                .collect(Collectors.groupingBy(Student::getCaste, Collectors.counting()));
        statistics.put("casteDistribution", casteDistribution);

        log.debug("Statistics calculated: {} total students", totalStudents);

        return statistics;
    }
}
