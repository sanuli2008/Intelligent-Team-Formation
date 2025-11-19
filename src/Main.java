// Main.java
//public class Main {
//
//    public static void main(String[] args) {
//
//        Scanner sc = new Scanner(System.in);
//        System.out.println("=== TeamMate Intelligent Team Formation System ===");
//        while (true) {
//            System.out.println("\nSelect user type:");
//            System.out.println("1) Participant");
//            System.out.println("2) Organizer");
//            System.out.println("3) Exit");
//            System.out.print("Choice: ");
//            String choice = sc.nextLine().trim();
//
//            switch (choice) {
//                case "1":
//                    ParticipantUI participantUI = new ParticipantUI("C:\\Users\\Sanuli\\Desktop\\Stage02\\CM2601[PRO]\\coursework\\Starter pack\\participants_sample.csv");
//                    participantUI.enterDetails();
//                    break;
//
//                case "2":
//                    OrganizerUI organizerUI = new OrganizerUI();
//                    organizerUI.uploadCSV();
//                    organizerUI.formTeams();
//                    organizerUI.viewResults();
//                    break;
//
//                case "3":
//                    System.out.println("Goodbye!");
//                    return;
//
//                default:
//                    System.out.println("Invalid choice. Try again.");
//            }
//        }
//    }
//}
//import java.util.Scanner;
//import UI.ParticipantUI;
//import UI.OrganizerUI;
//import java.io.File;
//
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        String membersFile = "C:\\Users\\pc\\Desktop\\Stage02\\CM2601[PRO]\\coursework\\Starter pack\\participants_sample.csv";
//
//        ParticipantUI pUI = new ParticipantUI(membersFile);
//        OrganizerUI oUI = new OrganizerUI();
//
//        while (true) {
//            System.out.println("\n=== TeamMate Menu ===");
//            System.out.println("1) Participant");
//            System.out.println("2) Organizer");
//            System.out.println("3) Participant: Find my team (by name or email)");
//            System.out.println("4) Organizer: Search member (by name/email)");
//            System.out.println("5) Exit");
//            System.out.print("Choice: ");
//            String choice = sc.nextLine().trim();
//
//            switch (choice) {
//                case "1":
//                    pUI.enterDetails();
//                    break;
//                case "2":
//                    oUI.uploadCSV();
//                    oUI.formTeams();
//                    oUI.viewResults();
//                    break;
//                case "3":
//                    System.out.print("Enter your name or email: ");
//                    String q = sc.nextLine().trim();
//                    // Try to find in formed_teams.csv first; if not present, instruct to ask organizer
//                    findTeamByNameOrEmail(q);
//                    break;
//                case "4":
//                    System.out.print("Enter search term for member: ");
//                    String s = sc.nextLine().trim();
//                    oUI.uploadCSV(); // ensure participants loaded
//                    oUI.searchMember(s);
//                    break;
//                case "5":
//                    System.out.println("Goodbye.");
//                    return;
//                default:
//                    System.out.println("Invalid choice.");
//            }
//        }
//    }
//
//    // search formed_teams.csv for the user's team
//    private static void findTeamByNameOrEmail(String query) {
//        File f = new File("formed_teams.csv");
//        if (!f.exists()) {
//            System.out.println("No formed teams file found. Organizer must form teams first.");
//            return;
//        }
//        String q = query.trim().toLowerCase();
//        boolean found = false;
//        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
//            String header = br.readLine();
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) continue;
//                String[] parts = line.split(",", -1);
//                // TeamID,ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType
//                String teamId = safe(parts,0);
//                String id = safe(parts,1);
//                String name = safe(parts,2);
//                String email = safe(parts,3);
//                if (name.toLowerCase().contains(q) || email.toLowerCase().contains(q) || id.toLowerCase().contains(q)) {
//                    System.out.println("Found: Team " + teamId + " | " + id + " | " + name + " | " + email);
//                    found = true;
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error searching formed_teams.csv: " + e.getMessage());
//            return;
//        }
//        if (!found) System.out.println("No team found for: " + query);
//    }
//
//    private static String safe(String[] a, int i) { if (a==null || i<0 || i>=a.length) return ""; return a[i]==null? "": a[i]; }
//}
import UI.OrganizerUI;
import UI.ParticipantUI;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String membersFile = "src/participants_sample.csv";
        ParticipantUI participantUI = new ParticipantUI(membersFile);
        OrganizerUI organizerUI = new OrganizerUI();

        while (true) {
            System.out.println("\n=== TeamMate ===");
            System.out.println("1) Participant");
            System.out.println("2) Organizer");
            System.out.println("3) Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    participantMenu(participantUI, sc);
                    break;
                case "2":
                    organizerMenu(organizerUI, sc);
                    break;
                case "3":
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void participantMenu(ParticipantUI pUI, Scanner sc) {
        while (true) {
            System.out.println("\n--- Participant ---");
            System.out.println("1) Register (take survey)");
            System.out.println("2) Find my team (by ID)");
            System.out.println("3) Back");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    pUI.enterDetails();
                    break;
                case "2":
                    System.out.print("Enter your ID (e.g., P001): ");
                    String id = sc.nextLine().trim();
                    findTeamForParticipant(id);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void organizerMenu(OrganizerUI oUI, Scanner sc) {
        while (true) {
            System.out.println("\n--- Organizer ---");
            System.out.println("1) Upload CSV");
            System.out.println("2) Form teams");
            System.out.println("3) View all teams");
            System.out.println("4) Search person (ID/Name/Email)");
            System.out.println("5) Search team (Team-# or number)");
            System.out.println("6) Back");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    oUI.uploadCSV();
                    break;
                case "2":
                    oUI.formTeams();
                    break;
                case "3":
                    oUI.viewResults();
                    break;
                case "4":
                    System.out.print("Enter search term (ID/Name/Email): ");
                    String q = sc.nextLine().trim();
                    oUI.searchMember(q);
                    break;
                case "5":
                    System.out.print("Enter team number or Team-#: ");
                    String t = sc.nextLine().trim();
                    oUI.searchTeam(t);
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void findTeamForParticipant(String query) {
        File f = new File("src/participants_sample.csv");
        if (!f.exists()) {
            System.out.println("members.csv not found. Organizer must create/load participants.");
            return;
        }
        String q = query.trim().toLowerCase();
        boolean found = false;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                String id = parts.length>0 ? parts[0].trim() : "";
                String name = parts.length>1 ? parts[1].trim() : "";
                String email = parts.length>2 ? parts[2].trim() : "";
                String assigned = parts.length>8 ? parts[8].trim() : "Unassigned";
                if (id.equalsIgnoreCase(q)) {
                    System.out.println("Found: " + id + " | " + name + " | " + email + " | Assigned: " + assigned);
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading members.csv: " + e.getMessage());
            return;
        }
        if (!found) System.out.println("ID not found: " + query);
    }
}
