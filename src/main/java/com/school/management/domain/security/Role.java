package com.school.management.domain.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of user roles in the School Management System.
 *
 * <p>Roles represent high-level job functions and are used for
 * coarse-grained access control. Each role has a set of associated
 * authorities (permissions) that define what actions users in that
 * role can perform.</p>
 *
 * <p>Role Hierarchy (from highest to lowest privilege):</p>
 * <ol>
 *   <li>ADMIN - System administrator with full access</li>
 *   <li>PRINCIPAL - School principal with management access</li>
 *   <li>OFFICE_STAFF - Office staff managing daily operations</li>
 *   <li>ACCOUNTS_MANAGER - Financial operations and fee management</li>
 *   <li>AUDITOR - Read-only access for auditing</li>
 * </ol>
 *
 * <p>Usage with Spring Security:</p>
 * <pre>
 * {@code
 * @PreAuthorize("hasRole('ADMIN')")
 * public void deleteUser(Long id) { ... }
 *
 * @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
 * public void approveExpense(Long id) { ... }
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public enum Role {
    /**
     * System Administrator.
     *
     * <p>Full system access including:</p>
     * <ul>
     *   <li>User management (create, update, delete users)</li>
     *   <li>System configuration</li>
     *   <li>Role and permission management</li>
     *   <li>All student, class, and fee operations</li>
     *   <li>Audit log access</li>
     * </ul>
     */
    ADMIN("Administrator", "Full system access"),

    /**
     * School Principal.
     *
     * <p>Management-level access including:</p>
     * <ul>
     *   <li>View all students, classes, and fees</li>
     *   <li>Approve fee waivers and discounts</li>
     *   <li>Generate reports</li>
     *   <li>View audit logs</li>
     *   <li>Manage class assignments</li>
     * </ul>
     */
    PRINCIPAL("Principal", "School management access"),

    /**
     * Office Staff.
     *
     * <p>Daily operations access including:</p>
     * <ul>
     *   <li>Create, update, and view students</li>
     *   <li>Manage class enrollments</li>
     *   <li>Record fee payments</li>
     *   <li>Generate receipts</li>
     *   <li>View reports</li>
     * </ul>
     */
    OFFICE_STAFF("Office Staff", "Daily operations access"),

    /**
     * Accounts Manager.
     *
     * <p>Financial operations access including:</p>
     * <ul>
     *   <li>View all fee records</li>
     *   <li>Record payments</li>
     *   <li>Generate financial reports</li>
     *   <li>Manage fee structures</li>
     *   <li>Process refunds</li>
     * </ul>
     */
    ACCOUNTS_MANAGER("Accounts Manager", "Financial operations access"),

    /**
     * Auditor.
     *
     * <p>Read-only access including:</p>
     * <ul>
     *   <li>View all records (students, classes, fees)</li>
     *   <li>View audit logs</li>
     *   <li>Generate reports</li>
     *   <li>No create, update, or delete permissions</li>
     * </ul>
     */
    AUDITOR("Auditor", "Read-only audit access");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the human-readable display name.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the role description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the Spring Security role name (with ROLE_ prefix).
     *
     * @return Spring Security role name
     */
    public String getAuthority() {
        return "ROLE_" + name();
    }

    /**
     * Gets the set of permissions associated with this role.
     *
     * @return set of permission names
     */
    public Set<String> getPermissions() {
        return switch (this) {
            case ADMIN -> Set.of(
                    Authority.STUDENT_MANAGE.name(),
                    Authority.CLASS_MANAGE.name(),
                    Authority.FEE_MANAGE.name(),
                    Authority.PAYMENT_MANAGE.name(),
                    Authority.USER_MANAGE.name(),
                    Authority.REPORT_MANAGE.name(),
                    Authority.AUDIT_MANAGE.name(),
                    Authority.SYSTEM_CONFIG.name(),
                    Authority.SYSTEM_SECURITY.name()
            );
            case PRINCIPAL -> Set.of(
                    Authority.STUDENT_READ.name(),
                    Authority.CLASS_READ.name(),
                    Authority.CLASS_UPDATE.name(),
                    Authority.FEE_READ.name(),
                    Authority.PAYMENT_READ.name(),
                    Authority.REPORT_STUDENT.name(),
                    Authority.REPORT_FINANCIAL.name(),
                    Authority.REPORT_AUDIT.name(),
                    Authority.AUDIT_READ.name()
            );
            case OFFICE_STAFF -> Set.of(
                    Authority.STUDENT_CREATE.name(),
                    Authority.STUDENT_READ.name(),
                    Authority.STUDENT_UPDATE.name(),
                    Authority.CLASS_READ.name(),
                    Authority.CLASS_UPDATE.name(),
                    Authority.FEE_READ.name(),
                    Authority.PAYMENT_CREATE.name(),
                    Authority.PAYMENT_READ.name(),
                    Authority.REPORT_STUDENT.name()
            );
            case ACCOUNTS_MANAGER -> Set.of(
                    Authority.FEE_CREATE.name(),
                    Authority.FEE_READ.name(),
                    Authority.FEE_UPDATE.name(),
                    Authority.PAYMENT_CREATE.name(),
                    Authority.PAYMENT_READ.name(),
                    Authority.PAYMENT_UPDATE.name(),
                    Authority.REPORT_FINANCIAL.name()
            );
            case AUDITOR -> Set.of(
                    Authority.STUDENT_READ.name(),
                    Authority.CLASS_READ.name(),
                    Authority.FEE_READ.name(),
                    Authority.PAYMENT_READ.name(),
                    Authority.REPORT_STUDENT.name(),
                    Authority.REPORT_FINANCIAL.name(),
                    Authority.REPORT_AUDIT.name(),
                    Authority.AUDIT_READ.name()
            );
        };
    }
}
