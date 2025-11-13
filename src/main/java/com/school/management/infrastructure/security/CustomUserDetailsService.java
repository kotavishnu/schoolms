package com.school.management.infrastructure.security;

import com.school.management.domain.user.User;
import com.school.management.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService for loading user-specific data.
 *
 * <p>This service is responsible for:</p>
 * <ul>
 *   <li>Loading user from database by username</li>
 *   <li>Checking if account is active</li>
 *   <li>Loading user authorities (roles and permissions)</li>
 *   <li>Creating Spring Security UserDetails object</li>
 * </ul>
 *
 * <p>Account Status Checks:</p>
 * <ul>
 *   <li>Account enabled/disabled (based on isActive)</li>
 *   <li>Account locked/unlocked (always unlocked, handled in AuthService)</li>
 *   <li>Account expired/not expired (never expires)</li>
 *   <li>Credentials expired/not expired (never expires)</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre>
 * {@code
 * @Autowired
 * private CustomUserDetailsService userDetailsService;
 *
 * UserDetails user = userDetailsService.loadUserByUsername("john.doe");
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user by username from database.
     *
     * <p>This method is called by Spring Security during authentication.
     * It retrieves the user from database and returns it as UserDetails.</p>
     *
     * <p>The User entity implements UserDetails interface, so it can be
     * returned directly.</p>
     *
     * @param username the username (case-sensitive)
     * @return UserDetails object (User entity)
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        log.debug("User loaded successfully: {} (role: {}, active: {})",
                user.getUsername(), user.getRole(), user.getIsActive());

        return user;
    }

    /**
     * Loads user by user ID (for token refresh scenarios).
     *
     * <p>This method is useful when validating refresh tokens which contain
     * user ID instead of username.</p>
     *
     * @param userId the user ID
     * @return UserDetails object (User entity)
     * @throws UsernameNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("User not found with ID: " + userId);
                });

        log.debug("User loaded successfully by ID: {} (username: {}, role: {})",
                userId, user.getUsername(), user.getRole());

        return user;
    }

    /**
     * Loads user by email (for password reset scenarios).
     *
     * <p>This method is useful for password reset flows where users
     * provide their email address.</p>
     *
     * @param email the email address (case-insensitive)
     * @return UserDetails object (User entity)
     * @throws UsernameNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("User loaded successfully by email: {} (username: {})",
                email, user.getUsername());

        return user;
    }
}

