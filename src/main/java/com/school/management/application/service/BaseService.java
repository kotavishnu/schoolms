package com.school.management.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface defining common CRUD operations.
 *
 * <p>This interface establishes a standard contract for service layer operations
 * across all domain entities. It promotes consistency and reduces code duplication.</p>
 *
 * <p>Type Parameters:</p>
 * <ul>
 *   <li>T - The DTO type for the domain entity</li>
 *   <li>ID - The type of the entity identifier</li>
 * </ul>
 *
 * <p>All application services should implement this interface:</p>
 * <pre>
 * {@code
 * @Service
 * public class StudentService implements BaseService<StudentDTO, Long> {
 *     // Implementation
 * }
 * }
 * </pre>
 *
 * <p>Standard Operations:</p>
 * <ul>
 *   <li>Create: {@link #create(Object)}</li>
 *   <li>Read: {@link #findById(Object)}, {@link #findAll()}, {@link #findAllPaged(Pageable)}</li>
 *   <li>Update: {@link #update(Object, Object)}</li>
 *   <li>Delete: {@link #deleteById(Object)}, {@link #delete(Object)}</li>
 * </ul>
 *
 * @param <T> the DTO type
 * @param <ID> the identifier type
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public interface BaseService<T, ID> {

    /**
     * Creates a new entity.
     *
     * @param dto the DTO containing entity data
     * @return the created entity DTO
     * @throws com.school.management.shared.exception.ValidationException if validation fails
     * @throws com.school.management.shared.exception.ConflictException if entity already exists
     */
    T create(T dto);

    /**
     * Updates an existing entity.
     *
     * @param id the entity ID
     * @param dto the DTO containing updated data
     * @return the updated entity DTO
     * @throws com.school.management.shared.exception.NotFoundException if entity not found
     * @throws com.school.management.shared.exception.ValidationException if validation fails
     */
    T update(ID id, T dto);

    /**
     * Finds an entity by ID.
     *
     * @param id the entity ID
     * @return Optional containing the entity DTO if found
     */
    Optional<T> findById(ID id);

    /**
     * Finds an entity by ID or throws NotFoundException.
     *
     * @param id the entity ID
     * @return the entity DTO (never null)
     * @throws com.school.management.shared.exception.NotFoundException if entity not found
     */
    T findByIdOrThrow(ID id);

    /**
     * Finds all entities.
     *
     * @return list of all entity DTOs
     */
    List<T> findAll();

    /**
     * Finds all entities with pagination.
     *
     * @param pageable pagination information
     * @return page of entity DTOs
     */
    Page<T> findAllPaged(Pageable pageable);

    /**
     * Deletes an entity by ID.
     *
     * @param id the entity ID
     * @throws com.school.management.shared.exception.NotFoundException if entity not found
     */
    void deleteById(ID id);

    /**
     * Deletes an entity.
     *
     * @param dto the entity DTO to delete
     * @throws com.school.management.shared.exception.NotFoundException if entity not found
     */
    void delete(T dto);

    /**
     * Checks if an entity exists by ID.
     *
     * @param id the entity ID
     * @return true if entity exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts all entities.
     *
     * @return total count of entities
     */
    long count();
}
