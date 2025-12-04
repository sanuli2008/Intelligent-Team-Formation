package Exceptions;
/**
 * Thrown when participant data is missing, malformed, or violates logical constraints
 * (e.g., Score > 100, invalid email format).
 */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}