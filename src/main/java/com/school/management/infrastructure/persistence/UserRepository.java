package com.school.management.infrastructure.persistence;

import com.school.management.domain.security.Role;
import com.school.management.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 *
 * <p>Provides data access operations for User entities including:</p>
 * <ul>
 *   <li>Find by username (for authentication)</li>
 *   <li>Find by email (for password reset, user lookup)</li>
 *   <li>Find active users by role</li>
 *   <li>Standard CRUD operations from BaseRepository</li>
 * </ul>
 *
 * <p>Custom Queries:</p>
 * <ul>
 *   <li>{@link #findByUsername(String)} - Case-sensitive username lookup</li>
 *   <li>{@link #findByEmail(String)} - Case-insensitive email lookup</li>
 *   <li>{@link #findByRoleAndIsActiveTrue(Role)} - Active users by role</li>
 * </ul>
 *
 * <p>Performance Considerations:</p>
 * <ul>
 *   <li>Username and email columns are indexed for fast lookup</li>
 *   <li>Role and isActive columns are indexed for filtering</li>
 *   <li>Queries use proper fetch strategies to avoid N+1 problems</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre>
 * {@code
 * @Service
 * public class UserService {
 *     @Autowired
 *     private UserRepository userRepository;
 *
 *     public User authenticate(String username, String password) {
 *         User user = userRepository.findByUsername(username)
 *             .orElseThrow(() -> new NotFoundException("User not found"));
 *         // Validate password...
 *         return user;
 *     }
 * }
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    /**
     * Finds a user by username (case-sensitive).
     *
     * <p>This method is primarily used for authentication.
     * Username lookup is case-sensitive as per security best practices.</p>
     *
     * <p>Performance: This query uses the idx_users_username index
     * for optimal performance.</p>
     *
     * @param username the username to search for (exact match, case-sensitive)
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address (case-insensitive).
     *
     * <p>This method is used for:</p>
     * <ul>
     *   <li>Password reset flows</li>
     *   <li>User lookup by email</li>
     *   <li>Email uniqueness validation</li>
     * </ul>
     *
     * <p>Performance: This query uses the idx_users_email index
     * for optimal performance. Case-insensitive search may not use
     * the index effectively in PostgreSQL; consider using a functional
     * index on LOWER(email) for better performance if needed.</p>
     *
     * @param email the email address to search for (case-insensitive)
     * @return Optional containing the user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Finds all active users with a specific role.
     *
     * <p>This method is used for:</p>
     * <ul>
     *   <li>Listing users by role (e.g., all active office staff)</li>
     *   <li>Role-based user management</li>
     *   <li>Generating role-specific reports</li>
     * </ul>
     *
     * <p>Performance: This query uses composite index on
     * (role, is_active) for optimal performance.</p>
     *
     * <p>Example Usage:</p>
     * <pre>
     * {@code
     * List<User> activeStaff = userRepository.findByRoleAndIsActiveTrue(Role.OFFICE_STAFF);
     * }
     * </pre>
     *
     * @param role the role to filter by
     * @return List of active users with the specified role (empty if none found)
     */
    List<User> findByRoleAndIsActiveTrue(Role role);

    /**
     * Checks if a user with the given username exists.
     *
     * <p>This is more efficient than findByUsername when you only
     * need to check existence without loading the entity.</p>
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email exists.
     *
     * <p>This is more efficient than findByEmail when you only
     * need to check existence without loading the entity.</p>
     *
     * @param email the email to check (case-insensitive)
     * @return true if user exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Counts the number of users with a specific role and active status.
     *
     * <p>Useful for statistics and reporting.</p>
     *
     * @param role the role to count
     * @param isActive whether to count active or inactive users
     * @return count of users matching the criteria
     */
    long countByRoleAndIsActive(Role role, Boolean isActive);
}
