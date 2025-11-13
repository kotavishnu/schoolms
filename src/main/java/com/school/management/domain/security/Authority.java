package com.school.management.domain.security;

/**
 * Enumeration of fine-grained authorities (permissions) in the system.
 *
 * <p>Authorities represent specific permissions for individual operations.
 * They provide fine-grained access control beyond role-based permissions.</p>
 *
 * <p>Authority Naming Convention:</p>
 * <ul>
 *   <li>Format: {RESOURCE}_{ACTION}</li>
 *   <li>RESOURCE: STUDENT, CLASS, FEE, USER, REPORT, AUDIT</li>
 *   <li>ACTION: CREATE, READ, UPDATE, DELETE, MANAGE</li>
 * </ul>
 *
 * <p>Usage with Method Security:</p>
 * <pre>
 * {@code
 * @PreAuthorize("hasAuthority('STUDENT_CREATE')")
 * public StudentDTO createStudent(StudentDTO dto) { ... }
 *
 * @PreAuthorize("hasAnyAuthority('STUDENT_READ', 'STUDENT_MANAGE')")
 * public StudentDTO getStudent(Long id) { ... }
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public enum Authority {
    // Student Authorities
    /**
     * Permission to create new students.
     */
    STUDENT_CREATE("Create students"),

    /**
     * Permission to view student information.
     */
    STUDENT_READ("View students"),

    /**
     * Permission to update existing students.
     */
    STUDENT_UPDATE("Update students"),

    /**
     * Permission to delete students.
     */
    STUDENT_DELETE("Delete students"),

    /**
     * Full permission to manage students (includes all student operations).
     */
    STUDENT_MANAGE("Manage all student operations"),

    // Class Authorities
    /**
     * Permission to create new classes.
     */
    CLASS_CREATE("Create classes"),

    /**
     * Permission to view class information.
     */
    CLASS_READ("View classes"),

    /**
     * Permission to update existing classes.
     */
    CLASS_UPDATE("Update classes"),

    /**
     * Permission to delete classes.
     */
    CLASS_DELETE("Delete classes"),

    /**
     * Full permission to manage classes.
     */
    CLASS_MANAGE("Manage all class operations"),

    // Fee Authorities
    /**
     * Permission to create fee structures.
     */
    FEE_CREATE("Create fee structures"),

    /**
     * Permission to view fee information.
     */
    FEE_READ("View fees"),

    /**
     * Permission to update fee structures.
     */
    FEE_UPDATE("Update fees"),

    /**
     * Permission to delete fee structures.
     */
    FEE_DELETE("Delete fees"),

    /**
     * Full permission to manage fees.
     */
    FEE_MANAGE("Manage all fee operations"),

    // Payment Authorities
    /**
     * Permission to record payments.
     */
    PAYMENT_CREATE("Record payments"),

    /**
     * Permission to view payment records.
     */
    PAYMENT_READ("View payments"),

    /**
     * Permission to update payment records.
     */
    PAYMENT_UPDATE("Update payments"),

    /**
     * Permission to delete/void payments.
     */
    PAYMENT_DELETE("Delete payments"),

    /**
     * Full permission to manage payments.
     */
    PAYMENT_MANAGE("Manage all payment operations"),

    // User Authorities
    /**
     * Permission to create new users.
     */
    USER_CREATE("Create users"),

    /**
     * Permission to view user information.
     */
    USER_READ("View users"),

    /**
     * Permission to update user information.
     */
    USER_UPDATE("Update users"),

    /**
     * Permission to delete users.
     */
    USER_DELETE("Delete users"),

    /**
     * Full permission to manage users.
     */
    USER_MANAGE("Manage all user operations"),

    // Report Authorities
    /**
     * Permission to generate student reports.
     */
    REPORT_STUDENT("Generate student reports"),

    /**
     * Permission to generate financial reports.
     */
    REPORT_FINANCIAL("Generate financial reports"),

    /**
     * Permission to generate audit reports.
     */
    REPORT_AUDIT("Generate audit reports"),

    /**
     * Full permission to generate all reports.
     */
    REPORT_MANAGE("Generate all reports"),

    // Audit Authorities
    /**
     * Permission to view audit logs.
     */
    AUDIT_READ("View audit logs"),

    /**
     * Permission to manage audit configuration.
     */
    AUDIT_MANAGE("Manage audit settings"),

    // System Authorities
    /**
     * Permission to manage system configuration.
     */
    SYSTEM_CONFIG("Manage system configuration"),

    /**
     * Permission to manage roles and permissions.
     */
    SYSTEM_SECURITY("Manage security settings");

    private final String description;

    Authority(String description) {
        this.description = description;
    }

    /**
     * Gets the authority description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the Spring Security authority name.
     *
     * @return authority name
     */
    public String getAuthority() {
        return name();
    }
}
