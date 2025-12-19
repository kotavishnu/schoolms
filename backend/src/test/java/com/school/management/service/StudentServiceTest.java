package com.school.management.service;

import com.school.management.dto.StudentDTO;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import com.school.management.repository.SchoolClassRepository;
import com.school.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository classRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentDTO studentDTO;
    private Student student;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        studentDTO = StudentDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .dob(LocalDate.of(2010, 5, 15))
                .address("123 Main St")
                .mobile("9876543210")
                .classId(1L)
                .build();

        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dob(LocalDate.of(2010, 5, 15))
                .address("123 Main St")
                .mobile("9876543210")
                .classId(1L)
                .status(StudentStatus.ACTIVE)
                .enrollmentDate(LocalDate.now())
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(40)
                .currentStrength(20)
                .build();
    }

    @Test
    @DisplayName("Should create student with valid data")
    void shouldCreateStudentWithValidData() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentDTO result = studentService.createStudent(studentDTO);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(classRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when class not found")
    void shouldThrowExceptionWhenClassNotFound() {
        // Given
        when(classRepository.findById(99L)).thenReturn(Optional.empty());
        studentDTO.setClassId(99L);

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> studentService.createStudent(studentDTO));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when class is full")
    void shouldThrowExceptionWhenClassIsFull() {
        // Given
        schoolClass.setCurrentStrength(40); // Full capacity
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When & Then
        assertThrows(BadRequestException.class,
                () -> studentService.createStudent(studentDTO));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should get student by ID")
    void shouldGetStudentById() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When
        StudentDTO result = studentService.getStudentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getFullName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> studentService.getStudentById(99L));
    }

    @Test
    @DisplayName("Should get all students")
    void shouldGetAllStudents() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(studentRepository.findAll()).thenReturn(students);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When
        List<StudentDTO> result = studentService.getAllStudents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should search students by name")
    void shouldSearchStudentsByName() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(studentRepository.searchByName("John")).thenReturn(students);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When
        List<StudentDTO> result = studentService.searchStudents("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).searchByName("John");
    }

    @Test
    @DisplayName("Should update student")
    void shouldUpdateStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentDTO.setId(1L);
        studentDTO.setFirstName("Jane");

        // When
        StudentDTO result = studentService.updateStudent(1L, studentDTO);

        // Then
        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student")
    void shouldDeleteStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(student);

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    @DisplayName("Should get students by class ID")
    void shouldGetStudentsByClassId() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(studentRepository.findByClassId(1L)).thenReturn(students);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When
        List<StudentDTO> result = studentService.getStudentsByClassId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findByClassId(1L);
    }
}
