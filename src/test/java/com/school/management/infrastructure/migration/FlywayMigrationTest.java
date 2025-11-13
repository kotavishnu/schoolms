package com.school.management.infrastructure.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Flyway database migrations.
 *
 * <p>Tests verify that:</p>
 * <ul>
 *   <li>All migrations execute successfully</li>
 *   <li>Migration scripts are in correct order (V1-V5)</li>
 *   <li>Database schema is created correctly</li>
 *   <li>All tables, indexes, and triggers are present</li>
 *   <li>Seed data is inserted properly</li>
 *   <li>Business rule constraints are enforced at database level</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Flyway Database Migration Tests")
class FlywayMigrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("school_test_db")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(false);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should successfully execute all Flyway migrations")
    void shouldExecuteAllMigrations() {
        // Given - Flyway is configured
        assertThat(flyway).isNotNull();

        // When - Get migration info
        MigrationInfoService infoService = flyway.info();
        MigrationInfo[] migrations = infoService.all();

        // Then - All migrations should be applied
        assertThat(migrations).isNotEmpty();
        assertThat(migrations).hasSizeGreaterThanOrEqualTo(5);

        for (MigrationInfo migration : migrations) {
            if (migration.getVersion() != null) {
                assertThat(migration.getState().isApplied())
                        .as("Migration %s should be applied", migration.getVersion())
                        .isTrue();
            }
        }
    }

    @Test
    @DisplayName("Should execute migrations in correct version order V1 to V5")
    void shouldExecuteMigrationsInCorrectOrder() {
        // When - Get migration info
        MigrationInfoService infoService = flyway.info();
        MigrationInfo[] migrations = infoService.all();

        // Then - Migrations should be in order V1, V2, V3, V4, V5
        assertThat(migrations).extracting(m -> m.getVersion() != null ? m.getVersion().getVersion() : null)
                .filteredOn(version -> version != null)
                .containsSequence("1", "2", "3", "4", "5");
    }

    @Test
    @DisplayName("Should create all expected database enums/types from V1")
    void shouldCreateAllDatabaseTypes() {
        // When - Query for custom types
        String query = """
                SELECT typname
                FROM pg_type
                WHERE typtype = 'e'
                ORDER BY typname
                """;

        List<String> types = jdbcTemplate.queryForList(query, String.class);

        // Then - All enums should exist
        assertThat(types).contains(
                "audit_action",
                "class_name",
                "config_category",
                "enrollment_status",
                "fee_frequency",
                "fee_type",
                "gender",
                "payment_mode",
                "payment_status",
                "relationship_type",
                "student_status",
                "user_role"
        );
    }

    @Test
    @DisplayName("Should create all core tables from V2")
    void shouldCreateAllCoreTables() {
        // When - Query for tables
        String query = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_type = 'BASE TABLE'
                ORDER BY table_name
                """;

        List<String> tables = jdbcTemplate.queryForList(query, String.class);

        // Then - All core tables should exist
        assertThat(tables).contains(
                "users",
                "academic_years",
                "classes",
                "students",
                "guardians",
                "enrollments",
                "fee_structures",
                "fee_journals",
                "payments",
                "receipts",
                "configurations",
                "audit_log"
        );
    }

    @Test
    @DisplayName("Should create all indexes from V3")
    void shouldCreateAllIndexes() {
        // When - Query for indexes (excluding primary key and unique constraints)
        String query = """
                SELECT indexname
                FROM pg_indexes
                WHERE schemaname = 'public'
                  AND indexname NOT LIKE '%_pkey'
                  AND indexname NOT LIKE '%uq_%'
                ORDER BY indexname
                """;

        List<String> indexes = jdbcTemplate.queryForList(query, String.class);

        // Then - Key indexes should exist
        assertThat(indexes).contains(
                "idx_students_student_code",
                "idx_students_mobile_hash",
                "idx_students_active_only",
                "idx_fee_journals_pending",
                "idx_fee_journals_overdue_only",
                "idx_receipts_receipt_number",
                "idx_users_username",
                "idx_classes_capacity_utilization"
        );
    }

    @Test
    @DisplayName("Should create all triggers from V4")
    void shouldCreateAllTriggers() {
        // When - Query for triggers
        String query = """
                SELECT DISTINCT trigger_name
                FROM information_schema.triggers
                WHERE trigger_schema = 'public'
                ORDER BY trigger_name
                """;

        List<String> triggers = jdbcTemplate.queryForList(query, String.class);

        // Then - All triggers should exist
        assertThat(triggers).contains(
                "audit_users_trigger",
                "audit_students_trigger",
                "audit_fee_journals_trigger",
                "audit_payments_trigger",
                "update_fee_journal_balance_trigger",
                "validate_payment_amount_trigger",
                "update_fee_journal_paid_amount_trigger",
                "enforce_single_current_academic_year_trigger",
                "enforce_single_primary_guardian_trigger"
        );
    }

    @Test
    @DisplayName("Should insert seed data from V5")
    void shouldInsertSeedData() {
        // When - Query for seed data counts
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Integer.class);
        Integer academicYearCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academic_years WHERE is_current = TRUE", Integer.class);
        Integer classCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM classes", Integer.class);
        Integer configCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM configurations", Integer.class);
        Integer feeStructureCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM fee_structures", Integer.class);

        // Then - Seed data should be present
        assertThat(userCount).isGreaterThanOrEqualTo(2); // admin + principal
        assertThat(academicYearCount).isEqualTo(1); // Current year 2024-2025
        assertThat(classCount).isEqualTo(10); // Classes 1A to 10A
        assertThat(configCount).isGreaterThan(20); // System configurations
        assertThat(feeStructureCount).isGreaterThan(0); // Fee structures
    }

    @Test
    @DisplayName("Should have admin user with correct credentials")
    void shouldHaveAdminUser() {
        // When - Query for admin user
        Map<String, Object> adminUser = jdbcTemplate.queryForMap(
                "SELECT username, full_name, role, is_active FROM users WHERE username = 'admin'");

        // Then - Admin user should exist with correct data
        assertThat(adminUser)
                .containsEntry("username", "admin")
                .containsEntry("full_name", "System Administrator")
                .containsEntry("role", "ADMIN")
                .containsEntry("is_active", true);
    }

    @Test
    @DisplayName("Should create current academic year 2024-2025")
    void shouldCreateCurrentAcademicYear() {
        // When - Query for current academic year
        Map<String, Object> academicYear = jdbcTemplate.queryForMap(
                "SELECT year_code, is_current FROM academic_years WHERE is_current = TRUE");

        // Then - Current academic year should be 2024-2025
        assertThat(academicYear)
                .containsEntry("year_code", "2024-2025")
                .containsEntry("is_current", true);
    }

    @Test
    @DisplayName("Should create default class sections (1A to 10A)")
    void shouldCreateDefaultClassSections() {
        // When - Query for class sections
        List<Map<String, Object>> classes = jdbcTemplate.queryForList(
                "SELECT class_name, section, max_capacity FROM classes ORDER BY class_name");

        // Then - Should have 10 classes (1A to 10A)
        assertThat(classes).hasSize(10);
        assertThat(classes).extracting(c -> c.get("section")).containsOnly("A");
        assertThat(classes).extracting(c -> c.get("max_capacity")).containsOnly(50);
    }

    @Test
    @DisplayName("Should create system configurations with correct categories")
    void shouldCreateSystemConfigurations() {
        // When - Query for configuration categories
        String query = """
                SELECT category, COUNT(*) as count
                FROM configurations
                GROUP BY category
                ORDER BY category
                """;

        List<Map<String, Object>> configsByCategory = jdbcTemplate.queryForList(query);

        // Then - Should have configurations in all categories
        assertThat(configsByCategory).isNotEmpty();
        assertThat(configsByCategory)
                .extracting(c -> c.get("category"))
                .contains("GENERAL", "ACADEMIC", "FINANCIAL", "SYSTEM");
    }

    @Test
    @DisplayName("Should enforce student code format constraint (BR-1)")
    void shouldEnforceStudentCodeFormat() {
        // Given - A student with invalid code format
        String invalidInsert = """
                INSERT INTO students (
                    student_code, first_name_encrypted, last_name_encrypted,
                    date_of_birth_encrypted, mobile_encrypted, mobile_hash,
                    admission_date, created_by, updated_by
                ) VALUES (
                    'INVALID',
                    decode('test', 'escape'),
                    decode('test', 'escape'),
                    decode('test', 'escape'),
                    decode('test', 'escape'),
                    'hash123',
                    CURRENT_DATE, 1, 1
                )
                """;

        // When/Then - Should throw constraint violation
        assertThat(org.assertj.core.api.Assertions.catchThrowable(() ->
                jdbcTemplate.execute(invalidInsert)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("chk_students_student_code");
    }

    @Test
    @DisplayName("Should enforce mobile number uniqueness via hash (BR-2)")
    void shouldEnforceMobileUniqueness() {
        // This test verifies the mobile_hash unique constraint exists
        String query = """
                SELECT constraint_name
                FROM information_schema.table_constraints
                WHERE table_name = 'students'
                  AND constraint_type = 'UNIQUE'
                  AND constraint_name LIKE '%mobile_hash%'
                """;

        List<String> constraints = jdbcTemplate.queryForList(query, String.class);

        // Then - Should have unique constraint on mobile_hash
        assertThat(constraints).isNotEmpty();
    }

    @Test
    @DisplayName("Should enforce class capacity constraints (BR-3)")
    void shouldEnforceClassCapacityConstraints() {
        // When - Query for class capacity check constraint
        String query = """
                SELECT check_clause
                FROM information_schema.check_constraints
                WHERE constraint_name = 'chk_classes_enrollment'
                """;

        List<String> checkClauses = jdbcTemplate.queryForList(query, String.class);

        // Then - Should have enrollment capacity check
        assertThat(checkClauses).isNotEmpty();
        assertThat(checkClauses.get(0))
                .contains("current_enrollment")
                .contains("max_capacity");
    }

    @Test
    @DisplayName("Should enforce payment amount validation (BR-9)")
    void shouldEnforcePaymentAmountValidation() {
        // When - Query for payment validation trigger
        String query = """
                SELECT trigger_name
                FROM information_schema.triggers
                WHERE trigger_name = 'validate_payment_amount_trigger'
                """;

        List<String> triggers = jdbcTemplate.queryForList(query, String.class);

        // Then - Payment validation trigger should exist
        assertThat(triggers).contains("validate_payment_amount_trigger");
    }

    @Test
    @DisplayName("Should enforce receipt number format (BR-11)")
    void shouldEnforceReceiptNumberFormat() {
        // When - Query for receipt number constraint
        String query = """
                SELECT check_clause
                FROM information_schema.check_constraints
                WHERE constraint_name = 'chk_receipts_receipt_number'
                """;

        List<String> checkClauses = jdbcTemplate.queryForList(query, String.class);

        // Then - Should have receipt number format check
        assertThat(checkClauses).isNotEmpty();
        assertThat(checkClauses.get(0)).contains("REC-");
    }

    @Test
    @DisplayName("Should have Flyway baseline version configured")
    void shouldHaveFlywayBaseline() {
        // When - Get Flyway baseline version
        MigrationInfoService infoService = flyway.info();

        // Then - Baseline should be configured
        assertThat(infoService).isNotNull();
        assertThat(infoService.current()).isNotNull();
    }

    @Test
    @DisplayName("Should have no pending migrations")
    void shouldHaveNoPendingMigrations() {
        // When - Get pending migrations
        MigrationInfoService infoService = flyway.info();
        MigrationInfo[] pending = infoService.pending();

        // Then - No migrations should be pending
        assertThat(pending).isEmpty();
    }

    @Test
    @DisplayName("Should validate migration checksums")
    void shouldValidateMigrationChecksums() {
        // When - Validate migrations
        MigrationInfoService infoService = flyway.info();

        // Then - All applied migrations should have valid checksums
        for (MigrationInfo migration : infoService.all()) {
            if (migration.getState().isApplied()) {
                assertThat(migration.getChecksum())
                        .as("Migration %s should have a checksum", migration.getVersion())
                        .isNotNull();
            }
        }
    }
}
