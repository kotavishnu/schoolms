package com.school.management.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for EncryptionService
 * Following TDD approach - tests written FIRST before implementation
 *
 * Tests AES-256-GCM encryption/decryption and SHA-256 hashing for mobile numbers
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EncryptionService Unit Tests")
class EncryptionServiceTest {

    private EncryptionService encryptionService;

    // Test encryption key (256-bit / 32 bytes)
    private static final String TEST_ENCRYPTION_KEY = "0123456789abcdef0123456789abcdef";

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        // Set encryption key using reflection for testing
        ReflectionTestUtils.setField(encryptionService, "encryptionKey", TEST_ENCRYPTION_KEY);
    }

    @Test
    @DisplayName("Should encrypt plaintext successfully")
    void shouldEncryptPlaintextSuccessfully() {
        // Arrange
        String plaintext = "John Doe";

        // Act
        byte[] encrypted = encryptionService.encrypt(plaintext);

        // Assert
        assertThat(encrypted).isNotNull();
        assertThat(encrypted.length).isGreaterThan(0);
        // Encrypted data should be different from plaintext
        assertThat(encrypted).isNotEqualTo(plaintext.getBytes());
    }

    @Test
    @DisplayName("Should decrypt ciphertext successfully")
    void shouldDecryptCiphertextSuccessfully() {
        // Arrange
        String plaintext = "Jane Smith";
        byte[] encrypted = encryptionService.encrypt(plaintext);

        // Act
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should encrypt and decrypt multiple times consistently")
    void shouldEncryptAndDecryptMultipleTimesConsistently() {
        // Arrange
        String plaintext = "Test Data 123";

        // Act & Assert - Multiple rounds
        for (int i = 0; i < 5; i++) {
            byte[] encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);
            assertThat(decrypted).isEqualTo(plaintext);
        }
    }

    @Test
    @DisplayName("Should produce different ciphertext for same plaintext (IV randomness)")
    void shouldProduceDifferentCiphertextForSamePlaintext() {
        // Arrange
        String plaintext = "Sensitive Data";

        // Act
        byte[] encrypted1 = encryptionService.encrypt(plaintext);
        byte[] encrypted2 = encryptionService.encrypt(plaintext);

        // Assert - Different IVs should produce different ciphertexts
        assertThat(encrypted1).isNotEqualTo(encrypted2);

        // But both should decrypt to same plaintext
        assertThat(encryptionService.decrypt(encrypted1)).isEqualTo(plaintext);
        assertThat(encryptionService.decrypt(encrypted2)).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should encrypt empty string")
    void shouldEncryptEmptyString() {
        // Arrange
        String plaintext = "";

        // Act
        byte[] encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should encrypt special characters")
    void shouldEncryptSpecialCharacters() {
        // Arrange
        String plaintext = "Test@#$%^&*()_+-=[]{}|;':\",./<>?";

        // Act
        byte[] encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should encrypt unicode characters")
    void shouldEncryptUnicodeCharacters() {
        // Arrange
        String plaintext = "Test 中文 हिंदी العربية";

        // Act
        byte[] encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should encrypt long text")
    void shouldEncryptLongText() {
        // Arrange
        String plaintext = "a".repeat(1000);

        // Act
        byte[] encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Should throw exception when encrypting null")
    void shouldThrowExceptionWhenEncryptingNull() {
        // Act & Assert
        assertThatThrownBy(() -> encryptionService.encrypt(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Plaintext cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when decrypting null")
    void shouldThrowExceptionWhenDecryptingNull() {
        // Act & Assert
        assertThatThrownBy(() -> encryptionService.decrypt(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ciphertext cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when decrypting invalid ciphertext")
    void shouldThrowExceptionWhenDecryptingInvalidCiphertext() {
        // Arrange
        byte[] invalidCiphertext = new byte[]{1, 2, 3};

        // Act & Assert
        assertThatThrownBy(() -> encryptionService.decrypt(invalidCiphertext))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw exception when decrypting tampered ciphertext (authentication failure)")
    void shouldThrowExceptionWhenDecryptingTamperedCiphertext() {
        // Arrange
        String plaintext = "Secure Data";
        byte[] encrypted = encryptionService.encrypt(plaintext);

        // Tamper with the ciphertext (modify last byte to simulate tampering)
        encrypted[encrypted.length - 1] = (byte) (encrypted[encrypted.length - 1] ^ 0xFF);

        // Act & Assert
        assertThatThrownBy(() -> encryptionService.decrypt(encrypted))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Decryption failed");
    }

    // SHA-256 Mobile Hash Tests

    @Test
    @DisplayName("Should hash mobile number successfully")
    void shouldHashMobileNumberSuccessfully() {
        // Arrange
        String mobile = "9876543210";

        // Act
        String hash = encryptionService.hashMobile(mobile);

        // Assert
        assertThat(hash).isNotNull();
        assertThat(hash).hasSize(64); // SHA-256 produces 64 hex characters
        assertThat(hash).matches("^[a-f0-9]{64}$"); // Hex string validation
    }

    @Test
    @DisplayName("Should produce same hash for same mobile number")
    void shouldProduceSameHashForSameMobileNumber() {
        // Arrange
        String mobile = "9876543210";

        // Act
        String hash1 = encryptionService.hashMobile(mobile);
        String hash2 = encryptionService.hashMobile(mobile);

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Should produce different hash for different mobile numbers")
    void shouldProduceDifferentHashForDifferentMobileNumbers() {
        // Arrange
        String mobile1 = "9876543210";
        String mobile2 = "9876543211";

        // Act
        String hash1 = encryptionService.hashMobile(mobile1);
        String hash2 = encryptionService.hashMobile(mobile2);

        // Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should throw exception when hashing null mobile")
    void shouldThrowExceptionWhenHashingNullMobile() {
        // Act & Assert
        assertThatThrownBy(() -> encryptionService.hashMobile(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile number cannot be null");
    }

    @Test
    @DisplayName("Should hash mobile numbers with consistent format")
    void shouldHashMobileNumbersWithConsistentFormat() {
        // Arrange
        String[] mobiles = {"1234567890", "9999999999", "0000000000"};

        // Act & Assert
        for (String mobile : mobiles) {
            String hash = encryptionService.hashMobile(mobile);
            assertThat(hash).hasSize(64);
            assertThat(hash).matches("^[a-f0-9]{64}$");
        }
    }

    @Test
    @DisplayName("Should encrypt date of birth as LocalDate")
    void shouldEncryptDateOfBirthAsLocalDate() {
        // Arrange
        String dateString = "2010-01-15";

        // Act
        byte[] encrypted = encryptionService.encrypt(dateString);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertThat(decrypted).isEqualTo(dateString);
    }

    @Test
    @DisplayName("Should handle concurrent encryption requests")
    void shouldHandleConcurrentEncryptionRequests() throws InterruptedException {
        // Arrange
        String plaintext = "Concurrent Test";
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    byte[] encrypted = encryptionService.encrypt(plaintext);
                    String decrypted = encryptionService.decrypt(encrypted);
                    results[index] = plaintext.equals(decrypted);
                } catch (Exception e) {
                    results[index] = false;
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        for (boolean result : results) {
            assertThat(result).isTrue();
        }
    }
}
