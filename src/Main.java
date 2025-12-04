import UI.ParticipantUI;
import UI.OrganizerUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Utils.AppLogger.setup();
        } catch (Exception e) {
            System.out.println("Warning: Logging could not be set up.");
        }
        ParticipantUI pUI = new ParticipantUI();
        OrganizerUI oUI = new OrganizerUI();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== TeamMate ===");
            System.out.println("1) Participant");
            System.out.println("2) Organizer");
            System.out.println("3) Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": pUI.participantMenu(); break;
                case "2": oUI.organizerMenu(); break;
                case "3": System.out.println("Goodbye!"); return;
                default: System.out.println("Please enter 1, 2 or 3.");
            }
        }
    }
}
