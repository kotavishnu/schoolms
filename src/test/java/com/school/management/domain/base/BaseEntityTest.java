package com.school.management.domain.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BaseEntity.
 *
 * <p>Tests verify:</p>
 * <ul>
 *   <li>Audit fields are properly initialized</li>
 *   <li>Created and updated timestamps are managed</li>
 *   <li>Created and updated by user IDs are tracked</li>
 *   <li>Equals and hashCode work correctly</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@DisplayName("BaseEntity Tests")
class BaseEntityTest {

    /**
     * Concrete implementation of BaseEntity for testing.
     */
    private static class TestEntity extends BaseEntity {
        private String name;

        public TestEntity() {
        }

        public TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    @DisplayName("Should create entity with audit fields initialized")
    void shouldCreateEntityWithAuditFields() {
        // When - Create new entity
        TestEntity entity = new TestEntity("Test");
        entity.setCreatedBy(1L);
        entity.setUpdatedBy(1L);

        // Then - Audit fields should be set
        assertThat(entity.getCreatedBy()).isEqualTo(1L);
        assertThat(entity.getUpdatedBy()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get createdAt timestamp")
    void shouldSetAndGetCreatedAt() {
        // Given - Entity and timestamp
        TestEntity entity = new TestEntity();
        LocalDateTime now = LocalDateTime.now();

        // When - Set createdAt
        entity.setCreatedAt(now);

        // Then - Should retrieve same value
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get updatedAt timestamp")
    void shouldSetAndGetUpdatedAt() {
        // Given - Entity and timestamp
        TestEntity entity = new TestEntity();
        LocalDateTime now = LocalDateTime.now();

        // When - Set updatedAt
        entity.setUpdatedAt(now);

        // Then - Should retrieve same value
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should track created by user ID")
    void shouldTrackCreatedByUserId() {
        // Given - Entity
        TestEntity entity = new TestEntity();

        // When - Set created by
        entity.setCreatedBy(123L);

        // Then - Should retrieve same value
        assertThat(entity.getCreatedBy()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Should track updated by user ID")
    void shouldTrackUpdatedByUserId() {
        // Given - Entity
        TestEntity entity = new TestEntity();

        // When - Set updated by
        entity.setUpdatedBy(456L);

        // Then - Should retrieve same value
        assertThat(entity.getUpdatedBy()).isEqualTo(456L);
    }

    @Test
    @DisplayName("Should implement equals correctly based on ID")
    void shouldImplementEqualsCorrectly() {
        // Given - Two entities with same ID
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        // When - Both have null IDs
        // Then - Should not be equal (transient entities)
        assertThat(entity1).isNotEqualTo(entity2);

        // When - Set same ID
        entity1.setId(1L);
        entity2.setId(1L);

        // Then - Should be equal
        assertThat(entity1).isEqualTo(entity2);

        // When - Different IDs
        entity2.setId(2L);

        // Then - Should not be equal
        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void shouldImplementHashCodeConsistently() {
        // Given - Entity with ID
        TestEntity entity = new TestEntity("Test");
        entity.setId(1L);

        // When - Get hash code multiple times
        int hash1 = entity.hashCode();
        int hash2 = entity.hashCode();

        // Then - Hash code should be consistent
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Should return false when comparing with null")
    void shouldReturnFalseWhenComparingWithNull() {
        // Given - Entity
        TestEntity entity = new TestEntity("Test");
        entity.setId(1L);

        // Then - Should not equal null
        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should return false when comparing with different class")
    void shouldReturnFalseWhenComparingWithDifferentClass() {
        // Given - Entity
        TestEntity entity = new TestEntity("Test");
        entity.setId(1L);

        // Then - Should not equal different class
        assertThat(entity).isNotEqualTo("string");
    }

    @Test
    @DisplayName("Should consider transient entities not equal")
    void shouldConsiderTransientEntitiesNotEqual() {
        // Given - Two transient entities (no ID set)
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        // Then - Should not be equal
        assertThat(entity1).isNotEqualTo(entity2);
    }
}
