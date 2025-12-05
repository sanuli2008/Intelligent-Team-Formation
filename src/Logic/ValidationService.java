package Logic;

import Exceptions.ValidationException;

/**
 * ValidationService - Provides centralized, reusable methods for checking business rules.
 */
public class ValidationService {

    private ValidationService() {
        // Utility class
    }

    // --- ID & FORMAT VALIDATION ---

    public static void validateIdFormat(String id) {
        if (id == null || id.trim().isEmpty()) {
            //sq number 1.3.1 in register use case of participant
            throw new ValidationException("Participant ID cannot be empty.");
        }
        if (!id.trim().matches("(?i)P\\d{3}")) {
            //sq number 1.3.2 in register use case of participant
            throw new ValidationException("Invalid ID format. Must be P followed by exactly 3 digits (e.g., P001).");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            //sq number 1.4.1 in register use case of participant
            throw new ValidationException("Name cannot be empty and must be at least 3 characters long.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.trim().matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            //sq number 1.5.1 in register use case of participant
            throw new ValidationException("Invalid email address format.");
        }
    }

    // --- VALUE RANGE VALIDATION ---

    public static void validateSkillLevel(int skill) {
        if (skill < 1 || skill > 10) {
            //sq number 1.6.1 in register use case of participant
            throw new ValidationException("Skill level must be between 1 and 10.");
        }
    }

    public static void validatePersonalityScore(int pscore) {
        if (pscore < 0 || pscore > 100) {
            throw new ValidationException("Personality Score must be between 0 and 100.");
        }
    }
}