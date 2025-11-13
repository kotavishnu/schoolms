package com.school.management.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Service for encrypting and decrypting sensitive PII data
 * Uses AES-256-GCM for authenticated encryption
 * Uses SHA-256 for generating searchable hashes
 *
 * @author School Management System
 * @version 1.0
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // 128 bits authentication tag
    private static final int GCM_IV_LENGTH = 12;   // 96 bits IV for GCM
    private static final int AES_KEY_SIZE = 256;    // 256 bits key

    @Value("${encryption.key:0123456789abcdef0123456789abcdef}")
    private String encryptionKey;

    private final SecureRandom secureRandom;

    public EncryptionService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Encrypts plaintext using AES-256-GCM
     *
     * @param plaintext the text to encrypt
     * @return byte array containing IV + encrypted data + authentication tag
     * @throws IllegalArgumentException if plaintext is null
     * @throws RuntimeException if encryption fails
     */
    public byte[] encrypt(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }

        try {
            // Generate random IV (nonce) for GCM
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Get secret key
            SecretKey secretKey = getSecretKey();

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

            // Encrypt the plaintext
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV + encrypted data (GCM tag is included in encryptedData)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return byteBuffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts ciphertext using AES-256-GCM
     *
     * @param ciphertext byte array containing IV + encrypted data + tag
     * @return decrypted plaintext
     * @throws IllegalArgumentException if ciphertext is null
     * @throws RuntimeException if decryption fails or authentication fails
     */
    public String decrypt(byte[] ciphertext) {
        if (ciphertext == null) {
            throw new IllegalArgumentException("Ciphertext cannot be null");
        }

        if (ciphertext.length < GCM_IV_LENGTH) {
            throw new RuntimeException("Decryption failed: Invalid ciphertext length");
        }

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertext);

            // Extract IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            // Extract encrypted data
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            // Get secret key
            SecretKey secretKey = getSecretKey();

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            // Decrypt and authenticate
            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a SHA-256 hash of mobile number for searchable index
     * This allows searching without decrypting all records
     *
     * @param mobile the mobile number to hash
     * @return 64-character hexadecimal hash string
     * @throws IllegalArgumentException if mobile is null
     * @throws RuntimeException if hashing fails
     */
    public String hashMobile(String mobile) {
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile number cannot be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(mobile.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);

        } catch (Exception e) {
            throw new RuntimeException("Hashing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the secret key from the configured encryption key
     *
     * @return SecretKey for AES encryption
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);

        // Ensure key is 256 bits (32 bytes)
        if (keyBytes.length != 32) {
            throw new IllegalStateException(
                "Encryption key must be exactly 32 bytes (256 bits). Current length: " + keyBytes.length);
        }

        return new SecretKeySpec(keyBytes, "AES");
    }
}
