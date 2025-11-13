package com.school.management.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * User information included in authentication response.
 *
 * <p>Contains essential user details and permissions.</p>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * User's unique identifier.
     */
    private Long userId;

    /**
     * User's username.
     */
    private String username;

    /**
     * User's full name.
     */
    private String fullName;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's role.
     */
    private String role;

    /**
     * User's permissions based on role.
     */
    private Set<String> permissions;
}
