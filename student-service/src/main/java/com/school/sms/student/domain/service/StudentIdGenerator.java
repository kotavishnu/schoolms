package com.school.sms.student.domain.service;

import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.time.Year;

/**
 * Domain service for generating unique Student IDs.
 * Format: STU-YYYY-XXXXX (e.g., STU-2024-00001)
 */
@Service
public class StudentIdGenerator {

    private final StudentRepository studentRepository;

    public StudentIdGenerator(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Generate next StudentId for the current year.
     *
     * @return new StudentId
     */
    public StudentId generateNext() {
        int currentYear = Year.now().getValue();
        String prefix = String.format("STU-%d-", currentYear);

        long count = studentRepository.countByStudentIdStartingWith(prefix);
        long nextSequence = count + 1;

        String studentIdValue = String.format("%s%05d", prefix, nextSequence);
        return StudentId.of(studentIdValue);
    }
}
