package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.valueobject.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for student status management operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentStatusService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Updates a student's status.
     *
     * @param studentId the student ID
     * @param status the new status
     * @param userId the user performing the operation
     * @return updated StudentDTO
     * @throws StudentNotFoundException if student not found
     */
    public StudentDTO updateStatus(String studentId, StudentStatus status, String userId) {
        log.info("Updating student status: {} to {}", studentId, status);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        // Update status
        if (status == StudentStatus.ACTIVE) {
            student.activate();
        } else {
            student.deactivate();
        }

        student.setUpdatedBy(userId);

        // Save
        Student updated = studentRepository.save(student);

        log.info("Student status updated successfully: {} to {}", studentId, status);
        return studentMapper.toDTO(updated);
    }
}
