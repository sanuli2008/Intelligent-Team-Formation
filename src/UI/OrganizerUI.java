package UI;

// ui/OrganizerUI.java
import java.util.*;
import java.io.*;
import Logic.*;
import Model.*;
import Interfaces.OrganizerInterface;


import java.util.*;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;

public class OrganizerUI implements OrganizerInterface {

    private String csvFile = "src/participants_sample.csv";
    private List<Participant> participants = new ArrayList<>();
    private List<Participant> invalidParticipants = new ArrayList<>();
    private final FileManager fileManager = new FileManager();
    private TeamBuilder builder;

    @Override
    public void uploadCSV() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV path (leave empty for members.csv): ");
        String path = sc.nextLine().trim();
        if (!path.isEmpty()) csvFile = path;
        Map<String, List<Participant>> map = fileManager.loadCSVWithValidation(csvFile);
        participants = map.getOrDefault("valid", new ArrayList<>());
        invalidParticipants = map.getOrDefault("invalid", new ArrayList<>());

        System.out.println("Loaded valid participants: " + participants.size());
        if (!invalidParticipants.isEmpty()) {
            System.out.println("Invalid rows detected (missing required fields): " + invalidParticipants.size());
            for (Participant p : invalidParticipants) {
                System.out.println(" - Invalid row (ID may be empty): " + p.getName() + " | email: " + p.getEmail());
            }
            System.out.println("Invalid rows will be ignored for team formation.");
        }
    }

    @Override
    public void formTeams() {
        if (participants == null || participants.isEmpty()) {
            System.out.println("No valid participants loaded. Upload CSV first.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        int teamSize = 0;
        while (true) {
            System.out.print("Enter desired team size (N): ");
            try {
                teamSize = Integer.parseInt(sc.nextLine().trim());
                if (teamSize >= 2) break;
                System.out.println("Enter an integer >= 2.");
            } catch (Exception e) {
                System.out.println("Enter a valid integer.");
            }
        }

        // Ensure each participant has an ID; if some missing, generate and set (rare)
        for (Participant p : participants) {
            if (p.getId() == null || p.getId().isEmpty()) {
                String nid = fileManager.getNextID(csvFile);
                p.setId(nid);
            }
        }

        builder = new TeamBuilder(teamSize, participants);
        builder.formTeams();

        // update participants' Assigned fields in participants list (builder does this on Participant objects)
        // overwrite members.csv with all participants (including invalid ones: keep them but mark Unassigned)
        // combine valid + invalid into a single list to write back: for invalid we keep original values and Assigned = Unassigned
        List<Participant> allForWrite = new ArrayList<>();
        allForWrite.addAll(participants); // valid participants now have Assigned possibly set
        // set invalid participants assigned = Unassigned
        for (Participant p : invalidParticipants) p.setAssigned("Unassigned");
        allForWrite.addAll(invalidParticipants);

        // overwrite members.csv
        fileManager.overwriteMembersCSV(csvFile, allForWrite);

        // save formed teams file
        fileManager.saveTeamsCSV(builder.getTeams(), "formed_teams.csv");

        System.out.println("Teams formed and saved. You can view teams or search.");
    }

    @Override
    public void viewResults() {
        if (builder == null || builder.getTeams().isEmpty()) {
            System.out.println("No teams formed yet. Run formTeams first.");
            return;
        }
        builder.displayAllTeams();
    }

    @Override
    public void searchMember(String query) {
        if (participants == null || participants.isEmpty()) {
            System.out.println("No participants loaded. Upload CSV first.");
            return;
        }
        String q = query.trim().toLowerCase();
        boolean found = false;
        for (Participant p : participants) {
            if ((p.getId() != null && p.getId().toLowerCase().contains(q))
                    || p.getName().toLowerCase().contains(q)
                    || p.getEmail().toLowerCase().contains(q)) {
                System.out.println("Found: " + p.getId() + " | " + p.getName() + " | Assigned: " + p.getAssigned());
                found = true;
            }
        }
        if (!found) System.out.println("No matching participant found for: " + query);
    }

    @Override
    public void searchTeam(String teamId) {
        if (builder == null || builder.getTeams().isEmpty()) {
            System.out.println("No teams formed yet.");
            return;
        }
        String q = teamId.trim().toLowerCase();
        boolean found = false;
        for (Team t : builder.getTeams()) {
            String tid = "team-" + t.getTeamID();
            if (tid.equalsIgnoreCase(q) || String.valueOf(t.getTeamID()).equals(q)) {
                System.out.println("Members of " + tid + ":");
                for (Participant p : t.getMembers()) {
                    System.out.println(" - " + p.getId() + " | " + p.getName() + " | " + p.getRole() + " | " + p.getGame());
                }
                found = true;
                break;
            }
        }
        if (!found) System.out.println("Team not found: " + teamId);
    }
}

//public class OrganizerUI implements OrganizerInterface {
//
//    private String csvFile = "C:\\Users\\pc\\Desktop\\Stage02\\CM2601[PRO]\\coursework\\Starter pack\\participants_sample.csv" ;
//    private List<Participant> participants = new ArrayList<>();
//    private final FileManager fileManager = new FileManager();
//    private final PersonalityClassifier classifier = new PersonalityClassifier();
//    private TeamBuilder builder;
//
//    @Override
//    public void uploadCSV() {
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter CSV path (leave empty for members.csv): ");
//        String path = sc.nextLine().trim();
//        if (!path.isEmpty()) csvFile = path;
//        participants = fileManager.loadCSV(csvFile);
//
//        // If some participants lack personality score or type, compute them (and update CSV).
//        boolean updated = false;
//        for (Participant p : participants) {
//            if (p.getPersonalityScore() <= 0 || p.getPersonalityType() == null || p.getPersonalityType().isEmpty()) {
//                // We cannot compute from answers if they weren't provided, but if score is 0, treat as needing computation
//                // We will ask the organizer whether to set default behavior: set unknowns via an interactive prompt? Simpler: classify with default
//                System.out.println("Participant " + p.getName() + " has missing personality fields. Setting defaults based on current score (if any).");
//                int score = p.getPersonalityScore();
//                if (score <= 0) {
//                    // no score – ask the organizer whether to ask this participant now (interactive) or skip
//                    System.out.print("Enter total score for " + p.getName() + " (or leave empty to set 60): ");
//                    String in = sc.nextLine().trim();
//                    if (!in.isEmpty()) {
//                        try { score = Integer.parseInt(in); } catch (Exception ex) { score = 60; }
//                    } else score = 60;
//                }
//                p.setPersonalityScore(score);
//                p.setPersonalityType(classifier.classify(score));
//                updated = true;
//            }
//        }
//        if (updated) {
//            // overwrite members.csv with the updated participants
//            fileManager.overwriteMembersCSV(csvFile, participants);
//        }
//
//        System.out.println("Loaded participants: " + participants.size());
//    }
//
//    @Override
//    public void formTeams() {
//        if (participants == null || participants.isEmpty()) {
//            System.out.println("No participants loaded. Run uploadCSV first.");
//            return;
//        }
//        Scanner sc = new Scanner(System.in);
//        int teamSize = 0;
//        while (true) {
//            System.out.print("Enter desired team size (N): ");
//            try {
//                teamSize = Integer.parseInt(sc.nextLine().trim());
//                if (teamSize < 1) throw new NumberFormatException();
//                break;
//            } catch (Exception e) {
//                System.out.println("Enter a positive integer.");
//            }
//        }
//        builder = new TeamBuilder(teamSize, participants);
//        builder.formTeams();
//
//        // Save full teams
//        fileManager.saveTeamsCSV(builder.getTeams(), "formed_teams.csv");
//
//        // Save leftovers
//        fileManager.saveParticipantsCSV(builder.getLeftoverParticipants(), "remaining.csv");
//
//        // After forming teams, update members.csv: assign team numbers maybe by rewriting members.csv to include team id?
//        // The brief wanted: people who got placed go to new file (formed_teams.csv) and remaining stay in same file.
//        // We already wrote formed_teams.csv and remaining.csv. We also will update members.csv to mark team assignment in a simple way:
//        // For clarity we will rewrite members.csv but keep original columns; participants who were placed will keep their rows unchanged (but they now exist in formed_teams.csv).
//        // Organizer can choose to remove placed participants from members.csv if desired. We'll ask:
//        System.out.print("Remove placed participants from members.csv? (y/n) [n]: ");
//        String rm = sc.nextLine().trim().toLowerCase();
//        if (rm.equals("y")) {
//            // remove all placed participants from members list and overwrite members.csv
//            Set<String> placedIds = builder.getTeams().stream()
//                    .flatMap(t -> t.getMembers().stream())
//                    .map(p -> p.getId())
//                    .collect(Collectors.toSet());
//            List<Participant> remaining = participants.stream()
//                    .filter(p -> !placedIds.contains(p.getId()))
//                    .collect(Collectors.toList());
//            fileManager.overwriteMembersCSV(csvFile, remaining);
//            System.out.println("Removed placed participants from members.csv; remaining count: " + remaining.size());
//        } else {
//            System.out.println("members.csv unchanged; placed participants are also in formed_teams.csv.");
//        }
//    }
//
//    @Override
//    public void viewResults() {
//        if (builder == null) {
//            System.out.println("No teams formed yet.");
//            return;
//        }
//        builder.displayAllTeams();
//        System.out.println("Formed teams saved to formed_teams.csv");
//    }
//
//    @Override
//    public void searchMember(String query) {
//        if (participants == null || participants.isEmpty()) {
//            System.out.println("No members loaded. Upload CSV first.");
//            return;
//        }
//        String q = query.trim().toLowerCase();
//        boolean found = false;
//        for (Participant p : participants) {
//            if (p.getName().toLowerCase().contains(q) || p.getEmail().toLowerCase().contains(q) || (p.getId()!=null && p.getId().toLowerCase().contains(q))) {
//                System.out.println(p);
//                found = true;
//            }
//        }
//        if (!found) System.out.println("No matching member found for: " + query);
//    }
//}

//public class OrganizerUI implements OrganizerInterface {
//
//    private String csvFile;
//    private List<Participant> participants;
//    private final FileManager fileManager = new FileManager();
//    private TeamBuilder builder;
//
//    @Override
//    public void uploadCSV() {
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter CSV file path to load (leave empty for default members.csv): ");
//        String path = sc.nextLine().trim();
//        csvFile = path.isEmpty() ? "C:\\Users\\Sanuli\\Desktop\\Stage02\\CM2601[PRO]\\coursework\\Starter pack\\participants_sample.csv" : path;
//
//        participants = fileManager.loadCSV(csvFile);
//        PersonalityClassifier classifier = new PersonalityClassifier();
//        for (Participant p : participants)
//            p.setPersonalityType(classifier.classify(p));
//
//        System.out.println("✅ Loaded " + participants.size() + " participants.\n");
//    }
//
//    @Override
//    public void formTeams() {
//        if (participants == null || participants.isEmpty()) {
//            System.out.println("⚠️ No participants loaded!");
//            return;
//        }
//
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter desired team size: ");
//        int size = Integer.parseInt(sc.nextLine());
//
//        builder = new TeamBuilder(size, participants);
//        builder.formTeams();
//
//        // save results
//        List<Team> formed = builder.getTeams();
//        fileManager.saveTeamsCSV(formed, "formed_teams.csv");
//
//        // keep leftovers (if any)
//        List<Participant> leftover = builder.getLeftoverParticipants();
//        if (!leftover.isEmpty())
//            fileManager.saveParticipantsCSV(leftover, "remaining.csv");
//    }
//
//    @Override
//    public void viewResults() {
//        if (builder == null) {
//            System.out.println("⚠️ No teams formed yet.");
//            return;
//        }
//        builder.displayAllTeams();
//        System.out.println("Results saved to formed_teams.csv");
//    }
//}
//