package com.school.management.presentation.rest;

import com.school.management.presentation.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Base controller providing common response helper methods.
 *
 * <p>This abstract class provides utility methods for creating consistent
 * HTTP responses across all REST controllers. It standardizes response
 * formatting and HTTP status code usage.</p>
 *
 * <p>All REST controllers should extend this class:</p>
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/api/students")
 * public class StudentController extends BaseController {
 *     // Implementation
 * }
 * }
 * </pre>
 *
 * <p>Response Methods:</p>
 * <ul>
 *   <li>{@link #ok(Object)} - 200 OK with data</li>
 *   <li>{@link #created(Object)} - 201 Created with data</li>
 *   <li>{@link #noContent()} - 204 No Content</li>
 *   <li>{@link #success(String, Object)} - 200 OK with message and data</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public abstract class BaseController {

    /**
     * Creates a 200 OK response with data.
     *
     * @param data the response data
     * @param <T> the type of response data
     * @return ResponseEntity with 200 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success("Operation completed successfully", data));
    }

    /**
     * Creates a 200 OK response with message and data.
     *
     * @param message the success message
     * @param data the response data
     * @param <T> the type of response data
     * @return ResponseEntity with 200 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(message, data));
    }

    /**
     * Creates a 200 OK response with only a message.
     *
     * @param message the success message
     * @param <T> the type of response data
     * @return ResponseEntity with 200 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(message));
    }

    /**
     * Creates a 201 Created response with data.
     *
     * @param data the created resource data
     * @param <T> the type of response data
     * @return ResponseEntity with 201 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", data));
    }

    /**
     * Creates a 201 Created response with message and data.
     *
     * @param message the success message
     * @param data the created resource data
     * @param <T> the type of response data
     * @return ResponseEntity with 201 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data));
    }

    /**
     * Creates a 204 No Content response.
     *
     * @param <T> the type of response data
     * @return ResponseEntity with 204 status
     */
    protected <T> ResponseEntity<Void> noContent() {
        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * Creates a 200 OK response for paginated data.
     *
     * <p>Includes pagination metadata in response headers:</p>
     * <ul>
     *   <li>X-Total-Count: Total number of items</li>
     *   <li>X-Total-Pages: Total number of pages</li>
     *   <li>X-Current-Page: Current page number</li>
     *   <li>X-Page-Size: Current page size</li>
     * </ul>
     *
     * @param page the page of data
     * @param <T> the type of page content
     * @return ResponseEntity with pagination headers
     */
    protected <T> ResponseEntity<ApiResponse<Page<T>>> okPaginated(Page<T> page) {
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .header("X-Current-Page", String.valueOf(page.getNumber()))
                .header("X-Page-Size", String.valueOf(page.getSize()))
                .body(ApiResponse.success("Data retrieved successfully", page));
    }

    /**
     * Creates a 200 OK response for paginated data with custom message.
     *
     * @param message the success message
     * @param page the page of data
     * @param <T> the type of page content
     * @return ResponseEntity with pagination headers
     */
    protected <T> ResponseEntity<ApiResponse<Page<T>>> okPaginated(String message, Page<T> page) {
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .header("X-Current-Page", String.valueOf(page.getNumber()))
                .header("X-Page-Size", String.valueOf(page.getSize()))
                .body(ApiResponse.success(message, page));
    }

    /**
     * Creates a 202 Accepted response.
     *
     * <p>Used for asynchronous operations that have been accepted
     * but not yet completed.</p>
     *
     * @param message the acceptance message
     * @param <T> the type of response data
     * @return ResponseEntity with 202 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> accepted(String message) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(message));
    }

    /**
     * Creates a 202 Accepted response with data.
     *
     * @param message the acceptance message
     * @param data additional data
     * @param <T> the type of response data
     * @return ResponseEntity with 202 status
     */
    protected <T> ResponseEntity<ApiResponse<T>> accepted(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(message, data));
    }
}
