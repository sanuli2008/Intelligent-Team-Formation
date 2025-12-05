package UI;

import Exceptions.ValidationException;
import Logic.FileManager;
import Logic.PersonalityClassifier;
import Logic.ValidationService;
import Model.Participant;

import java.util.Map;
import java.util.Scanner;

/**
 * ParticipantUI - friendly participant console interface
 *
 * Behavior:
 *  - Allow registration with ID validation (unique)
 *  - Ask survey Qs one-by-one
 *  - Allow participant to find their assigned team (privacy: only show own data)
 */
public class ParticipantUI {
    private final FileManager fm = new FileManager();
    private final PersonalityClassifier classifier = new PersonalityClassifier();
    private final Scanner sc = new Scanner(System.in);

    public ParticipantUI() {
        fm.ensureMasterExists();
    }

    public void participantMenu() {
        while (true) {
            System.out.println("\n--- Participant Menu ---");
            System.out.println("1) Register (complete survey)");
            System.out.println("2) Find my team by ID");
            System.out.println("3) Back to main menu");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            if ("1".equals(c)) register();
            else if ("2".equals(c)) findTeamById();
            else if ("3".equals(c)) return;
            else System.out.println("Please choose 1, 2, or 3.");
        }
    }

    private void register() {
        System.out.println("\n=== Participant Registration ===");
        fm.ensureMasterExists();
        Map<String, Participant> master = fm.readMasterMap();

        String id;
        while (true) {
            System.out.print("Enter your ID (P###): ");
            id = sc.nextLine().trim();
            try {
                // Delegation: Use ValidationService for format check
                ValidationService.validateIdFormat(id);

                if (master.containsKey(id)) {
                    System.out.println("This ID already exists in the system. You must register with a unique ID or contact organizer.");
                    continue;
                }
                break;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        String name;
        while (true) {
            System.out.print("Full name: ");
            name = sc.nextLine().trim();
            try {
                ValidationService.validateName(name);
                break;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine().trim();
            try {
                ValidationService.validateEmail(email);
                break;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.print("Preferred game/sport (e.g., FIFA, Chess): ");
        String game = sc.nextLine().trim();
        // Game must be provided
        if (game.isEmpty()) game = "Unknown";
        else if (game.length() < 2) {
            System.out.println("Game/Sport name is too short. Setting to Unknown.");
            game = "Unknown";
        }

        String role;
        while (true) {
            System.out.println("Choose preferred role (type exactly): Strategist, Attacker, Defender, Supporter, Coordinator");
            System.out.print("Role: ");
            role = sc.nextLine().trim();
            // Role validation
            if (role.equalsIgnoreCase("Strategist") || role.equalsIgnoreCase("Attacker") ||
                    role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Supporter") ||
                    role.equalsIgnoreCase("Coordinator")) break;
            System.out.println("Invalid role. Please use one of the listed roles.");
        }

        int skill = 0;
        while (true) {
            System.out.print("Skill level (1-10): ");
            try {
                skill = Integer.parseInt(sc.nextLine().trim());
                // Delegation: Use ValidationService for range check
                ValidationService.validateSkillLevel(skill);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Enter integer between 1 and 10.");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\nPlease answer the following five statements (1 = Strongly Disagree ... 5 = Strongly Agree). Answer one-by-one.");
        int q1 = askQ("Q1: I enjoy taking the lead and guiding others during group activities.");
        int q2 = askQ("Q2: I prefer analyzing situations and coming up with strategic solutions.");
        int q3 = askQ("Q3: I work well with others and enjoy collaborative teamwork.");
        int q4 = askQ("Q4: I am calm under pressure and can help maintain team morale.");
        int q5 = askQ("Q5: I like making quick decisions and adapting in dynamic situations.");

        int score = classifier.computeScore(q1,q2,q3,q4,q5);
        String type = classifier.classify(score);

        // Reject registration for users with undefined personality type (Score < 50)
        if ("Undefined".equalsIgnoreCase(type)) {
            System.out.println("\n[Regret] Your personality score (" + score + ") does not meet the criteria (min 50) for classification.");
            System.out.println("Registration cannot be completed at this time.");
            return;
        }

        // Save if Valid
        // ID is guaranteed to be non-empty and non-duplicate here
        Participant p = new Participant(id, name, email, game, skill, role, score, type, "Unassigned");
        String assignedId = fm.appendNewParticipant(p);

        if (assignedId != null) {
            System.out.println("Registration successful! ID: " + assignedId + " | Type: " + type);
        } else {
            System.out.println("Error saving participant. Check master file or contact organizer.");
        }
    }

    private int askQ(String prompt) {
        while (true) {
            System.out.print(prompt + " (1-5): ");
            try { int v = Integer.parseInt(sc.nextLine().trim()); if (v >=1 && v <=5) return v; } catch (Exception ignored) {}
            System.out.println("Please answer with an integer between 1 and 5.");
        }
    }

    private void findTeamById() {
        System.out.print("Enter your ID (P###): ");
        String id = sc.nextLine().trim();
        Map<String, Participant> master = fm.readMasterMap();
        if (master.containsKey(id)) {
            Participant p = master.get(id);
            System.out.println("Name: " + p.getName() + " | Assigned: " + p.getAssigned());
        } else {
            System.out.println("ID not found. If you recently registered, ensure you saved the registration or contact the organizer.");
        }
    }
}
