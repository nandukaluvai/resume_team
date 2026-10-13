package com.pms.service;

/**
 * DataStorageException
 * Thrown when an error occurs during profile file operations (saving, loading, serialization).
 * 
 * Demonstrates:
 * - Exception hierarchy and robust error encapsulation
 */
public class DataStorageException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataStorageException(String message) {
        super(message);
    }

    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
