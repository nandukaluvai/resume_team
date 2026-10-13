package com.pms.service;

/**
 * ValidationException
 * Thrown when user profile input fails validation checks.
 * 
 * Demonstrates:
 * - Custom checked Exception handling in Java
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
