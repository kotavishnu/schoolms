package com.school.management.domain.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for StudentCodeGenerator
 * Tests student code generation with format STU-YYYY-NNNNN
 */
@DisplayName("StudentCodeGenerator Tests")
class StudentCodeGeneratorTest {

    private StudentCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new StudentCodeGenerator();
    }

    @Test
    @DisplayName("Should generate student code with correct format")
    void shouldGenerateStudentCodeWithCorrectFormat() {
        // Act
        String studentCode = generator.generate();

        // Assert
        assertThat(studentCode).isNotNull();
        assertThat(studentCode).matches("^STU-\\d{4}-\\d{5}$");
    }

    @Test
    @DisplayName("Should generate student code with current year")
    void shouldGenerateStudentCodeWithCurrentYear() {
        // Arrange
        int currentYear = LocalDate.now().getYear();

        // Act
        String studentCode = generator.generate();

        // Assert
        assertThat(studentCode).startsWith("STU-" + currentYear + "-");
    }

    @Test
    @DisplayName("Should generate sequential student codes")
    void shouldGenerateSequentialStudentCodes() {
        // Act
        String code1 = generator.generate();
        String code2 = generator.generate();
        String code3 = generator.generate();

        // Extract sequence numbers
        int seq1 = extractSequenceNumber(code1);
        int seq2 = extractSequenceNumber(code2);
        int seq3 = extractSequenceNumber(code3);

        // Assert
        assertThat(seq2).isEqualTo(seq1 + 1);
        assertThat(seq3).isEqualTo(seq2 + 1);
    }

    @Test
    @DisplayName("Should generate unique student codes")
    void shouldGenerateUniqueStudentCodes() {
        // Arrange
        Set<String> codes = new HashSet<>();
        int generateCount = 100;

        // Act
        for (int i = 0; i < generateCount; i++) {
            codes.add(generator.generate());
        }

        // Assert
        assertThat(codes).hasSize(generateCount);
    }

    @Test
    @DisplayName("Should pad sequence number with leading zeros")
    void shouldPadSequenceNumberWithLeadingZeros() {
        // Act
        String code1 = generator.generate();

        // Extract sequence part
        String sequencePart = code1.substring(code1.lastIndexOf('-') + 1);

        // Assert
        assertThat(sequencePart).hasSize(5);
        assertThat(sequencePart).matches("\\d{5}");
    }

    @Test
    @DisplayName("Should start sequence from 00001")
    void shouldStartSequenceFrom00001() {
        // Arrange
        StudentCodeGenerator newGenerator = new StudentCodeGenerator();

        // Act
        String firstCode = newGenerator.generate();
        int sequenceNumber = extractSequenceNumber(firstCode);

        // Assert
        assertThat(sequenceNumber).isEqualTo(1);
        assertThat(firstCode).endsWith("00001");
    }

    @Test
    @DisplayName("Should handle concurrent generation safely")
    void shouldHandleConcurrentGenerationSafely() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        int codesPerThread = 10;
        Set<String> allCodes = new HashSet<>();
        Thread[] threads = new Thread[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < codesPerThread; j++) {
                    synchronized (allCodes) {
                        allCodes.add(generator.generate());
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertThat(allCodes).hasSize(threadCount * codesPerThread);
    }

    @Test
    @DisplayName("Should generate codes in ascending order")
    void shouldGenerateCodesInAscendingOrder() {
        // Act
        String code1 = generator.generate();
        String code2 = generator.generate();
        String code3 = generator.generate();
        String code4 = generator.generate();
        String code5 = generator.generate();

        // Assert
        assertThat(code1).isLessThan(code2);
        assertThat(code2).isLessThan(code3);
        assertThat(code3).isLessThan(code4);
        assertThat(code4).isLessThan(code5);
    }

    @Test
    @DisplayName("Should handle large sequence numbers")
    void shouldHandleLargeSequenceNumbers() {
        // Arrange
        for (int i = 0; i < 99998; i++) {
            generator.generate();
        }

        // Act
        String code = generator.generate();

        // Assert
        assertThat(code).matches("^STU-\\d{4}-\\d{5}$");
        assertThat(extractSequenceNumber(code)).isEqualTo(99999);
    }

    @Test
    @DisplayName("Should generate consistent format across multiple calls")
    void shouldGenerateConsistentFormatAcrossMultipleCalls() {
        // Arrange
        Pattern pattern = Pattern.compile("^STU-\\d{4}-\\d{5}$");

        // Act & Assert
        for (int i = 0; i < 50; i++) {
            String code = generator.generate();
            assertThat(code).matches(pattern);
        }
    }

    @Test
    @DisplayName("Should include hyphen separators")
    void shouldIncludeHyphenSeparators() {
        // Act
        String code = generator.generate();

        // Assert
        assertThat(code).contains("-");
        assertThat(code.split("-")).hasSize(3);
    }

    /**
     * Helper method to extract sequence number from student code
     */
    private int extractSequenceNumber(String studentCode) {
        String[] parts = studentCode.split("-");
        return Integer.parseInt(parts[2]);
    }
}
