package com.school.sms.student.infrastructure.persistence.adapter;

import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.valueobject.StudentStatus;
import com.school.sms.student.infrastructure.persistence.repository.JpaStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of StudentRepository.
 * Bridges domain repository interface with Spring Data JPA repository.
 */
@Component
@RequiredArgsConstructor
public class StudentRepositoryAdapter implements StudentRepository {

    private final JpaStudentRepository jpaRepository;

    @Override
    public Student save(Student student) {
        return jpaRepository.save(student);
    }

    @Override
    public Optional<Student> findById(String studentId) {
        return jpaRepository.findById(studentId);
    }

    @Override
    public Page<Student> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Student> findByStatus(StudentStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Student> findByLastNameStartingWithIgnoreCase(String lastName, Pageable pageable) {
        return jpaRepository.findByLastNameStartingWithIgnoreCase(lastName, pageable);
    }

    @Override
    public void deleteById(String studentId) {
        jpaRepository.findById(studentId).ifPresent(student -> {
            student.deactivate();
            jpaRepository.save(student);
        });
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return jpaRepository.existsByMobile(mobile);
    }

    @Override
    public boolean existsByMobileAndStudentIdNot(String mobile, String studentId) {
        return jpaRepository.existsByMobileAndStudentIdNot(mobile, studentId);
    }

    @Override
    public List<Student> findAll() {
        return jpaRepository.findAll();
    }
}
