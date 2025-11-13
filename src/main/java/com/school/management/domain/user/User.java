package com.school.management.domain.user;

import com.school.management.domain.base.BaseEntity;
import com.school.management.domain.security.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity representing system users with role-based access control.
 *
 * <p>This entity implements Spring Security's UserDetails interface for
 * seamless integration with authentication and authorization mechanisms.</p>
 *
 * <p>Supported Roles:</p>
 * <ul>
 *   <li>ADMIN - System administrator with full access</li>
 *   <li>PRINCIPAL - School principal with management access</li>
 *   <li>OFFICE_STAFF - Office staff managing daily operations</li>
 *   <li>ACCOUNTS_MANAGER - Financial operations and fee management</li>
 *   <li>AUDITOR - Read-only access for auditing</li>
 * </ul>
 *
 * <p>Security Features:</p>
 * <ul>
 *   <li>Username and email uniqueness constraints</li>
 *   <li>BCrypt password hashing (handled in service layer)</li>
 *   <li>Account activation/deactivation</li>
 *   <li>Login tracking with lastLoginAt</li>
 *   <li>Password change tracking with passwordChangedAt</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre>
 * {@code
 * User user = User.builder()
 *     .username("john.doe")
 *     .passwordHash(passwordEncoder.encode("password123"))
 *     .fullName("John Doe")
 *     .email("john.doe@school.com")
 *     .mobile("+1234567890")
 *     .role(Role.OFFICE_STAFF)
 *     .isActive(true)
 *     .build();
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_role", columnList = "role"),
    @Index(name = "idx_users_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"passwordHash"})
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false, updatable = false))
})
public class User extends BaseEntity implements UserDetails {

    /**
     * Unique username for authentication.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Required (not null or blank)</li>
     *   <li>Must be unique across all users</li>
     *   <li>Maximum length: 50 characters</li>
     *   <li>Used for login authentication</li>
     * </ul>
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username size must be between 3 and 50 characters")
    private String username;

    /**
     * BCrypt hashed password.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Required (not null)</li>
     *   <li>Should be BCrypt hashed with salt >= 12</li>
     *   <li>Stored as hashed value, never plain text</li>
     *   <li>Excluded from toString() for security</li>
     * </ul>
     */
    @Column(nullable = false, length = 255)
    @NotBlank(message = "Password hash must not be blank")
    private String passwordHash;

    /**
     * Full name of the user.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Required (not null or blank)</li>
     *   <li>Maximum length: 100 characters</li>
     *   <li>Used for display purposes</li>
     * </ul>
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Full name must not be blank")
    @Size(max = 100, message = "Full name size must not exceed 100 characters")
    private String fullName;

    /**
     * Email address of the user.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Optional (can be null)</li>
     *   <li>Must be valid email format if provided</li>
     *   <li>Must be unique across all users</li>
     *   <li>Maximum length: 100 characters</li>
     * </ul>
     */
    @Column(unique = true, length = 100)
    @Email(message = "Email must be a well-formed email address")
    @Size(max = 100, message = "Email size must not exceed 100 characters")
    private String email;

    /**
     * Mobile phone number.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Optional (can be null)</li>
     *   <li>Maximum length: 20 characters</li>
     *   <li>Format validation done in service layer</li>
     * </ul>
     */
    @Column(length = 20)
    @Size(max = 20, message = "Mobile size must not exceed 20 characters")
    private String mobile;

    /**
     * User role for access control.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Required (not null)</li>
     *   <li>One of: ADMIN, PRINCIPAL, OFFICE_STAFF, ACCOUNTS_MANAGER, AUDITOR</li>
     *   <li>Determines user permissions and authorities</li>
     *   <li>Stored as string in database</li>
     * </ul>
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must not be null")
    private Role role;

    /**
     * Account activation status.
     *
     * <p>Constraints:</p>
     * <ul>
     *   <li>Required (not null)</li>
     *   <li>Defaults to true</li>
     *   <li>Inactive users cannot authenticate</li>
     *   <li>Used by isEnabled() method for Spring Security</li>
     * </ul>
     */
    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Timestamp of last successful login.
     *
     * <p>Usage:</p>
     * <ul>
     *   <li>Optional (can be null)</li>
     *   <li>Updated on successful authentication</li>
     *   <li>Used for activity monitoring</li>
     *   <li>Updated via recordLogin() method</li>
     * </ul>
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Timestamp of last password change.
     *
     * <p>Usage:</p>
     * <ul>
     *   <li>Optional (can be null)</li>
     *   <li>Updated when password is changed</li>
     *   <li>Used for password expiration policies</li>
     *   <li>Updated via changePassword() method</li>
     * </ul>
     */
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    /**
     * Records a successful login by updating lastLoginAt timestamp.
     *
     * <p>This method should be called by the authentication service
     * after successful user authentication.</p>
     */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Changes the user's password and records the change timestamp.
     *
     * <p>This method should be called by the password change service.
     * The newPasswordHash should already be BCrypt hashed.</p>
     *
     * @param newPasswordHash the new BCrypt hashed password
     */
    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
    }

    /**
     * Deactivates the user account.
     *
     * <p>Deactivated users cannot authenticate or perform any actions.
     * This is a soft delete mechanism.</p>
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Activates the user account.
     *
     * <p>Activated users can authenticate and perform actions based on
     * their role permissions.</p>
     */
    public void activate() {
        this.isActive = true;
    }

    // UserDetails interface implementation

    /**
     * Returns the authorities granted to the user based on their role.
     *
     * <p>Each role is mapped to a Spring Security GrantedAuthority
     * with the format "ROLE_{ROLE_NAME}".</p>
     *
     * @return collection containing the user's role authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority(role.getAuthority())
        );
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the BCrypt hashed password
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * <p>Currently, accounts do not expire. This always returns true.</p>
     *
     * @return true (accounts never expire)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * <p>Currently, accounts are not locked. This always returns true.
     * Account lockout after failed login attempts is handled separately
     * in the authentication service.</p>
     *
     * @return true (accounts are never locked)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * <p>Currently, credentials do not expire. This always returns true.
     * Password expiration policies can be implemented using passwordChangedAt.</p>
     *
     * @return true (credentials never expire)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * <p>Disabled users cannot authenticate. This is controlled by the
     * isActive field.</p>
     *
     * @return true if user is active, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
