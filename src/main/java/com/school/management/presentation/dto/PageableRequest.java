package com.school.management.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for pagination parameters in API requests.
 *
 * <p>This class standardizes pagination across all list endpoints:</p>
 * <ul>
 *   <li>page: Zero-based page number (default: 0)</li>
 *   <li>size: Number of records per page (default: 20, max: 100)</li>
 *   <li>sort: List of sort specifications (field,direction)</li>
 * </ul>
 *
 * <p>Usage in Controller:</p>
 * <pre>
 * {@code
 * @GetMapping
 * public ApiResponse<Page<StudentDTO>> getStudents(
 *         @Valid PageableRequest pageableRequest) {
 *     Pageable pageable = pageableRequest.toPageable();
 *     Page<StudentDTO> students = studentService.findAllPaged(pageable);
 *     return ApiResponse.success("Students retrieved", students);
 * }
 * }
 * </pre>
 *
 * <p>API Request Examples:</p>
 * <pre>
 * GET /api/students?page=0&size=20
 * GET /api/students?page=1&size=50&sort=firstName,asc&sort=lastName,desc
 * GET /api/students?size=10&sort=createdAt,desc
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {

    /**
     * Zero-based page number.
     * Default: 0 (first page)
     */
    @Min(value = 0, message = "Page number must be non-negative")
    @Builder.Default
    private int page = 0;

    /**
     * Number of records per page.
     * Default: 20
     * Maximum: 100 (to prevent excessive load)
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private int size = 20;

    /**
     * List of sort specifications.
     * Format: "field,direction" (e.g., "firstName,asc", "createdAt,desc")
     * Multiple sort fields supported.
     */
    @Builder.Default
    private List<String> sort = new ArrayList<>();

    /**
     * Converts this request to Spring Data Pageable.
     *
     * @return Spring Data Pageable instance
     */
    public Pageable toPageable() {
        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(page, size);
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortSpec : sort) {
            Sort.Order order = parseSortSpecification(sortSpec);
            if (order != null) {
                orders.add(order);
            }
        }

        Sort sorting = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        return PageRequest.of(page, size, sorting);
    }

    /**
     * Parses a sort specification string into a Sort.Order.
     *
     * <p>Format: "field,direction"</p>
     * <p>Examples:</p>
     * <ul>
     *   <li>"firstName,asc" → Sort by firstName ascending</li>
     *   <li>"createdAt,desc" → Sort by createdAt descending</li>
     *   <li>"id" → Sort by id ascending (default)</li>
     * </ul>
     *
     * @param sortSpec the sort specification string
     * @return Sort.Order instance, or null if invalid
     */
    private Sort.Order parseSortSpecification(String sortSpec) {
        if (sortSpec == null || sortSpec.trim().isEmpty()) {
            return null;
        }

        String[] parts = sortSpec.split(",");
        String field = parts[0].trim();

        if (field.isEmpty()) {
            return null;
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            String directionStr = parts[1].trim().toUpperCase();
            if ("DESC".equals(directionStr)) {
                direction = Sort.Direction.DESC;
            }
        }

        return new Sort.Order(direction, field);
    }

    /**
     * Creates a PageableRequest with default values.
     *
     * @return default PageableRequest (page=0, size=20)
     */
    public static PageableRequest defaultPageable() {
        return PageableRequest.builder()
                .page(0)
                .size(20)
                .build();
    }

    /**
     * Creates a PageableRequest with custom page and size.
     *
     * @param page page number (zero-based)
     * @param size page size
     * @return PageableRequest with specified values
     */
    public static PageableRequest of(int page, int size) {
        return PageableRequest.builder()
                .page(page)
                .size(size)
                .build();
    }

    /**
     * Creates a PageableRequest with custom page, size, and sort.
     *
     * @param page page number (zero-based)
     * @param size page size
     * @param sort list of sort specifications
     * @return PageableRequest with specified values
     */
    public static PageableRequest of(int page, int size, List<String> sort) {
        return PageableRequest.builder()
                .page(page)
                .size(size)
                .sort(sort != null ? sort : new ArrayList<>())
                .build();
    }
}
