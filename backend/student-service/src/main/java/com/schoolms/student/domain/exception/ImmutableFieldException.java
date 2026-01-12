package com.schoolms.student.domain.exception;

/**
 * Exception thrown when attempting to modify immutable fields (BE-011)
 */
public class ImmutableFieldException extends RuntimeException {
    public ImmutableFieldException(String fieldName) {
        super(String.format("Field '%s' is immutable and cannot be modified", fieldName));
    }
}
