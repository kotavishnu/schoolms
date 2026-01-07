package com.school.management.service;

import com.school.management.dto.StudentDTO;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import com.school.management.repository.SchoolClassRepository;
import com.school.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;

    public StudentDTO createStudent(StudentDTO dto) {
        // Validate class exists
        SchoolClass schoolClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", dto.getClassId()));

        // Check if class has capacity
        if (schoolClass.getCurrentStrength() >= schoolClass.getCapacity()) {
            throw new BadRequestException("Class is full. Cannot enroll more students.");
        }

        // Create student entity
        Student student = Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dob(dto.getDob())
                .address(dto.getAddress())
                .caste(dto.getCaste())
                .mobile(dto.getMobile())
                .religion(dto.getReligion())
                .molesOnBody(dto.getMolesOnBody())
                .motherName(dto.getMotherName())
                .fatherName(dto.getFatherName())
                .classId(dto.getClassId())
                .enrollmentDate(dto.getEnrollmentDate() != null ? dto.getEnrollmentDate() : LocalDate.now())
                .status(dto.getStatus() != null ? dto.getStatus() : StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Student savedStudent = studentRepository.save(student);

        // Update class strength
        schoolClass.setCurrentStrength(schoolClass.getCurrentStrength() + 1);
        classRepository.save(schoolClass);

        return convertToDTO(savedStudent, schoolClass);
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        SchoolClass schoolClass = classRepository.findById(student.getClassId())
                .orElse(null);

        return convertToDTO(student, schoolClass);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(student -> {
                    SchoolClass schoolClass = classRepository.findById(student.getClassId())
                            .orElse(null);
                    return convertToDTO(student, schoolClass);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> searchStudents(String query) {
        List<Student> students = studentRepository.searchByName(query);
        return students.stream()
                .map(student -> {
                    SchoolClass schoolClass = classRepository.findById(student.getClassId())
                            .orElse(null);
                    return convertToDTO(student, schoolClass);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByClassId(Long classId) {
        List<Student> students = studentRepository.findByClassId(classId);
        SchoolClass schoolClass = classRepository.findById(classId).orElse(null);

        return students.stream()
                .map(student -> convertToDTO(student, schoolClass))
                .collect(Collectors.toList());
    }

    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // If class is being changed, validate new class
        if (!existingStudent.getClassId().equals(dto.getClassId())) {
            SchoolClass newClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", dto.getClassId()));

            if (newClass.getCurrentStrength() >= newClass.getCapacity()) {
                throw new BadRequestException("New class is full. Cannot transfer student.");
            }

            // Update class strengths
            SchoolClass oldClass = classRepository.findById(existingStudent.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", existingStudent.getClassId()));
            oldClass.setCurrentStrength(oldClass.getCurrentStrength() - 1);
            newClass.setCurrentStrength(newClass.getCurrentStrength() + 1);
            classRepository.save(oldClass);
            classRepository.save(newClass);
        }

        // Update student fields
        existingStudent.setFirstName(dto.getFirstName());
        existingStudent.setLastName(dto.getLastName());
        existingStudent.setDob(dto.getDob());
        existingStudent.setAddress(dto.getAddress());
        existingStudent.setCaste(dto.getCaste());
        existingStudent.setMobile(dto.getMobile());
        existingStudent.setReligion(dto.getReligion());
        existingStudent.setMolesOnBody(dto.getMolesOnBody());
        existingStudent.setMotherName(dto.getMotherName());
        existingStudent.setFatherName(dto.getFatherName());
        existingStudent.setClassId(dto.getClassId());
        existingStudent.setUpdatedAt(LocalDateTime.now());

        if (dto.getStatus() != null) {
            existingStudent.setStatus(dto.getStatus());
        }

        Student updatedStudent = studentRepository.save(existingStudent);
        SchoolClass schoolClass = classRepository.findById(updatedStudent.getClassId()).orElse(null);

        return convertToDTO(updatedStudent, schoolClass);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // Update class strength
        SchoolClass schoolClass = classRepository.findById(student.getClassId())
                .orElse(null);
        if (schoolClass != null && schoolClass.getCurrentStrength() > 0) {
            schoolClass.setCurrentStrength(schoolClass.getCurrentStrength() - 1);
            classRepository.save(schoolClass);
        }

        studentRepository.delete(student);
    }

    private StudentDTO convertToDTO(Student student, SchoolClass schoolClass) {
        StudentDTO dto = StudentDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dob(student.getDob())
                .address(student.getAddress())
                .caste(student.getCaste())
                .mobile(student.getMobile())
                .religion(student.getReligion())
                .molesOnBody(student.getMolesOnBody())
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .classId(student.getClassId())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .fullName(student.getFullName())
                .build();

        if (schoolClass != null) {
            dto.setClassName("Class " + schoolClass.getClassNumber() +
                    (schoolClass.getSection() != null ? " " + schoolClass.getSection() : ""));
        }

        return dto;
    }
}
