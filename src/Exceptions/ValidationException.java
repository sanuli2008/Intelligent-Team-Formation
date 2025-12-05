package Exceptions;

/**
 * Thrown when a specific validation rule fails (e.g., input format, range check).
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}