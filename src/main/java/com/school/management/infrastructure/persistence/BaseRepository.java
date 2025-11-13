package com.school.management.infrastructure.persistence;

import com.school.management.domain.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for all domain entities.
 *
 * <p>This interface extends both JpaRepository and JpaSpecificationExecutor
 * to provide comprehensive data access capabilities including:</p>
 * <ul>
 *   <li>Basic CRUD operations (findById, save, delete, etc.)</li>
 *   <li>Pagination and sorting support</li>
 *   <li>Dynamic query construction using Specifications</li>
 *   <li>Batch operations</li>
 * </ul>
 *
 * <p>All domain-specific repositories should extend this interface:</p>
 * <pre>
 * {@code
 * public interface StudentRepository extends BaseRepository<Student, Long> {
 *     Optional<Student> findByStudentCode(String studentCode);
 * }
 * }
 * </pre>
 *
 * <p>Common query methods are already provided:</p>
 * <ul>
 *   <li>{@link #findAllByIdIn(List)} - Find all entities by IDs</li>
 *   <li>{@link #existsByIdIn(List)} - Check if any entity exists by IDs</li>
 *   <li>{@link #countByIdIn(List)} - Count entities by IDs</li>
 *   <li>{@link #deleteByIdIn(List)} - Delete entities by IDs</li>
 * </ul>
 *
 * @param <T> the domain entity type extending BaseEntity
 * @param <ID> the type of the entity identifier
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends
        JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {

    /**
     * Finds all entities with the given IDs.
     *
     * <p>This method is useful for batch retrieval operations.</p>
     *
     * @param ids the list of entity IDs
     * @return list of entities (empty if none found)
     */
    List<T> findAllByIdIn(List<ID> ids);

    /**
     * Checks if any entity exists with the given IDs.
     *
     * <p>This is more efficient than loading entities when you only
     * need to check existence.</p>
     *
     * @param ids the list of entity IDs
     * @return true if at least one entity exists, false otherwise
     */
    boolean existsByIdIn(List<ID> ids);

    /**
     * Counts entities with the given IDs.
     *
     * @param ids the list of entity IDs
     * @return count of entities
     */
    long countByIdIn(List<ID> ids);

    /**
     * Deletes all entities with the given IDs.
     *
     * <p>This method performs batch deletion which is more efficient
     * than deleting entities one by one.</p>
     *
     * @param ids the list of entity IDs to delete
     * @return number of entities deleted
     */
    long deleteByIdIn(List<ID> ids);

    /**
     * Finds an entity by ID or throws NotFoundException.
     *
     * <p>This is a convenience method that avoids Optional handling
     * in service layer when you always expect the entity to exist.</p>
     *
     * @param id the entity ID
     * @return the entity (never null)
     * @throws com.school.management.shared.exception.NotFoundException if entity not found
     */
    default T findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new com.school.management.shared.exception.NotFoundException(
                        "Entity", id.toString()));
    }
}
