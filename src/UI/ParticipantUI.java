package UI;

import java.util.Scanner;
import Interfaces.ParticipantInterface;
import Logic.FileManager;
import Logic.PersonalityClassifier;
import Model.Participant;
import java.util.Scanner;

public class ParticipantUI implements ParticipantInterface {

    private final String csvFile;
    private final FileManager fileManager = new FileManager();
    private final PersonalityClassifier classifier = new PersonalityClassifier();

    public ParticipantUI(String csvFile) { this.csvFile = csvFile; }

    @Override
    public void enterDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== Participant Registration ===");

        String name;
        while (true) {
            System.out.print("Full name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) break;
            System.out.println("Name cannot be empty.");
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine().trim();
            if (email.contains("@") && email.contains(".")) break;
            System.out.println("Enter a valid email.");
        }

        System.out.print("Preferred Game: ");
        String game = sc.nextLine().trim();
        if (game.isEmpty()) game = "Unknown";

        String role;
        while (true) {
            System.out.println("Preferred Role (type exact): Strategist, Attacker, Defender, Supporter, Coordinator");
            System.out.print("Role: ");
            role = sc.nextLine().trim();
            if (role.equalsIgnoreCase("Strategist") || role.equalsIgnoreCase("Attacker")
                    || role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Supporter")
                    || role.equalsIgnoreCase("Coordinator")) break;
            System.out.println("Invalid role. Choose one of the listed roles.");
        }

        int skill = 0;
        while (true) {
            System.out.print("Skill level (1-10): ");
            try {
                skill = Integer.parseInt(sc.nextLine().trim());
                if (skill >= 1 && skill <= 10) break;
                System.out.println("Enter 1..10.");
            } catch (Exception e) {
                System.out.println("Enter a number 1..10.");
            }
        }

        System.out.println("\nAnswer the following 5 questions (1 Strongly Disagree .. 5 Strongly Agree).");
        int q1 = askQ(sc, "Q1: I enjoy taking the lead and guiding others during group activities.");
        int q2 = askQ(sc, "Q2: I prefer analyzing situations and coming up with strategic solutions.");
        int q3 = askQ(sc, "Q3: I work well with others and enjoy collaborative teamwork.");
        int q4 = askQ(sc, "Q4: I am calm under pressure and can help maintain team morale.");
        int q5 = askQ(sc, "Q5: I like making quick decisions and adapting in dynamic situations.");

        int score = classifier.computeScoreFromAnswers(q1,q2,q3,q4,q5); // scaled to 20..100
        String type = classifier.classify(score);

        Participant p = new Participant(name, email, game, skill, role, score, type);
        String assignedId = fileManager.appendParticipant(csvFile, p);
        System.out.println("Registration complete. Your ID: " + assignedId + " | Personality: " + type + " | Assigned: Unassigned");
    }

    private int askQ(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (1-5): ");
            try {
                int a = Integer.parseInt(sc.nextLine().trim());
                if (a >= 1 && a <= 5) return a;
            } catch (Exception e) {}
            System.out.println("Enter 1..5.");
        }
    }
}

//public class ParticipantUI implements ParticipantInterface {
//
//    private final String csvFile;
//    private final FileManager fileManager = new FileManager();
//    private final PersonalityClassifier classifier = new PersonalityClassifier();
//
//    public ParticipantUI(String csvFile) {
//        this.csvFile = csvFile;
//    }
//
//    @Override
//    public void enterDetails() {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("\n=== Participant Survey ===");
//        System.out.print("Full name: ");
//        String name = sc.nextLine().trim();
//        System.out.print("Email: ");
//        String email = sc.nextLine().trim();
//        System.out.print("Preferred Game: ");
//        String game = sc.nextLine().trim();
//
//        // Roles: show options
//        System.out.println("Choose preferred role (type exact): Strategist, Attacker, Defender, Supporter, Coordinator");
//        System.out.print("Role: ");
//        String role = sc.nextLine().trim();
//
//        int skill = 0;
//        while (true) {
//            System.out.print("Skill level (1-10): ");
//            try {
//                skill = Integer.parseInt(sc.nextLine().trim());
//                if (skill < 1 || skill > 10) throw new NumberFormatException();
//                break;
//            } catch (Exception e) {
//                System.out.println("Enter a number between 1 and 10.");
//            }
//        }
//
//        System.out.println("\nAnswer the 5 personality questions (1 Strongly Disagree ... 5 Strongly Agree).");
//        System.out.println("Q1: I enjoy taking the lead and guiding others during group activities.");
//        System.out.println("Q2: I prefer analyzing situations and coming up with strategic solutions.");
//        System.out.println("Q3: I work well with others and enjoy collaborative teamwork.");
//        System.out.println("Q4: I am calm under pressure and can help maintain team morale.");
//        System.out.println("Q5: I like making quick decisions and adapting in dynamic situations.");
//
//        int q1 = askQ(sc, "Q1");
//        int q2 = askQ(sc, "Q2");
//        int q3 = askQ(sc, "Q3");
//        int q4 = askQ(sc, "Q4");
//        int q5 = askQ(sc, "Q5");
//
//        int score = classifier.computeScoreFromAnswers(q1,q2,q3,q4,q5); // scaled 20..100 or 5*4
//        String type = classifier.classify(score);
//
//        Participant p = new Participant(name, email, game, skill, role, score, type);
//        String newId = fileManager.appendParticipant(csvFile, p);
//
//        System.out.println("Thanks! Your ID: " + newId + " Personality type: " + type);
//    }
//
//    private int askQ(Scanner sc, String q) {
//        while (true) {
//            System.out.print(q + " (1-5): ");
//            try {
//                int a = Integer.parseInt(sc.nextLine().trim());
//                if (a < 1 || a > 5) throw new NumberFormatException();
//                return a;
//            } catch (Exception e) {
//                System.out.println("Enter 1..5.");
//            }
//        }
//    }
//}

//// ui/ParticipantUI.java
//import java.io.*;
//import java.util.Scanner;
//
//import Interfaces.ParticipantInterface;
//import Model.Participant;
//import Logic.PersonalityClassifier;
//
//public class ParticipantUI implements ParticipantInterface {
//
//    private final String csvFile;
//    private final PersonalityClassifier classifier = new PersonalityClassifier();
//
//    public ParticipantUI(String csvFile) {
//        this.csvFile = csvFile;
//    }
//
//    @Override
//    public void enterDetails() {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("\n--- Participant Survey ---");
//
//        System.out.print("Name: ");        String name = sc.nextLine();
//        System.out.print("Game/Sport: ");  String game = sc.nextLine();
//        System.out.print("Preferred Role: "); String role = sc.nextLine();
//        System.out.print("Skill level (0-100): ");
//        int skill = Integer.parseInt(sc.nextLine());
//
//        // five personality questions → numeric answers
//        int score = 0;
//        for (int i = 1; i <= 5; i++) {
//            System.out.print("Q" + i + " (1-20): ");
//            score += Integer.parseInt(sc.nextLine());
//        }
//
//        Participant p = new Participant(name, game, role, skill, score);
//        p.setPersonalityType(classifier.classify(p));
//
//        // append to CSV
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
//            bw.write(p.getName() + "," + p.getGame() + "," + p.getRole() + ","
//                    + p.getSkillLevel() + "," + p.getPersonalityScore() + ","
//                    + p.getPersonalityType());
//            bw.newLine();
//            System.out.println("✅ Your response has been recorded.\n");
//        } catch (IOException e) {
//            System.out.println("❌ Could not write to file: " + e.getMessage());
//        }
//    }
//}

//public class ParticipantUI {
//
//    private final String csvFile;
//    private final FileManager fileManager = new FileManager();
//    private final PersonalityClassifier classifier = new PersonalityClassifier();
//
//    public ParticipantUI(String csvFile) {
//        this.csvFile = csvFile;
//    }
//
//    public void enterDetails() {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("\n--- Participant Survey ---");
//        System.out.print("Name: "); String name = sc.nextLine();
//        System.out.print("Email: "); String email = sc.nextLine();
//        System.out.print("Preferred Game: "); String game = sc.nextLine();
//        System.out.print("Preferred Role: "); String role = sc.nextLine();
//        System.out.print("Skill Level (0-5): "); int skill = Integer.parseInt(sc.nextLine());
//
//        int score = 0;
//        for (int i=1;i<=5;i++) {
//            System.out.print("Personality Question " + i + " (1-20): ");
//            score += Integer.parseInt(sc.nextLine());
//        }
//
//        String type = classifier.classify(score);
//        Participant p = new Participant(name, email, game, skill, role, score, type);
//        fileManager.appendParticipant(csvFile, p);
//
//    }
//}

