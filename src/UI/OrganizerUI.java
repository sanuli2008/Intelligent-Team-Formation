package UI;

import Logic.FileManager;
import Logic.PersonalityClassifier;
import Threads.SurveyProcessor;
import Threads.TeamFormationWorker;
import Model.Participant;
import Model.Team;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class OrganizerUI {
    private static final Logger LOGGER = Logger.getLogger(OrganizerUI.class.getName());
    private final Scanner sc = new Scanner(System.in);
    private final FileManager fm = new FileManager();
    private final PersonalityClassifier classifier = new PersonalityClassifier();

    private Map<String, Participant> masterMap = new LinkedHashMap<>();
    private List<Participant> preparedCandidates = new ArrayList<>();
    private String lastUploadedFilePath = null;
    private List<Participant> lastUploadedRows = null;
    private List<Team> currentTeams = new ArrayList<>();
    private List<Participant> currentLeftovers = new ArrayList<>();

    private static final String SAMPLE_UPLOADED_PATH = "src/all_participants.csv";

    public OrganizerUI() {
        fm.ensureMasterExists();
        masterMap = fm.readMasterMap();
    }

    public void organizerMenu() {
        while (true) {
            System.out.println("\n--- Organizer Menu ---");
            System.out.println("1) Load master to form teams with unassigned participants");
            System.out.println("2) Upload participants CSV and prepare candidates");
            System.out.println("3) Form teams");
            System.out.println("4) View formed teams");
            System.out.println("5) Export formed teams");
            System.out.println("6) Search participant in master");
            System.out.println("7) Reset Tournament (Clear Assignments)");
            System.out.println("8) Back to main menu");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();

            switch (c) {
                case "1": loadMaster(); break;
                case "2": uploadAndPrepare(); break;
                case "3": formTeams(); break;
                case "4": viewTeams(); break;
                case "5": exportFormedTeams(); break;
                case "6": searchParticipant(); break;
                case "7": resetTournament(); break;
                case "8": return;
                default: System.out.println("Enter a number 1..7.");
            }
        }
    }

    //to form teams with unassigned participants using master file
    private void loadMaster() {
        fm.ensureMasterExists();
        masterMap = fm.readMasterMap();
        System.out.println("Master loaded. Participants: " + masterMap.size());
    }

    //sq number 1 in upload participant data use case of organizer
    //to upload an external file
    private void uploadAndPrepare() {
        System.out.print("Enter CSV path to upload: ");
        String path = sc.nextLine().trim();
        if (path.isEmpty()) path = SAMPLE_UPLOADED_PATH;

        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File not found.");
            return;
        }

        //sq number 1.1 in upload participant data use case of organizer
        masterMap = fm.readMasterMap();

        try {
            //sq number 1.2 in upload participant data use case of organizer
            //to prevent uploading already uploaded file
            List<Participant> validCandidates = fm.importCandidates(path, masterMap);

            if (validCandidates.isEmpty()) {
                System.out.println("No new candidates found (all duplicates or invalid).");
                return;
            }

            lastUploadedFilePath = path;
            lastUploadedRows = validCandidates;
            preparedCandidates = new ArrayList<>(validCandidates);
            LOGGER.info("User uploaded file: " + path + ". Valid candidates: " + validCandidates.size());
            System.out.println("Prepared " + preparedCandidates.size() + " NEW candidate(s).");


        } catch (Exceptions.InvalidDataException e) {
            System.out.println("[Error] Import failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error] Unexpected error: " + e.getMessage());
        }
    }

    //form teams with valid participants
    //sq number 1 in form teams use case of organizer
    private void formTeams() {
        List<Participant> source = new ArrayList<>();

        if (!preparedCandidates.isEmpty()) {
            source.addAll(preparedCandidates);
            System.out.println("Forming teams from prepared candidates (" + source.size() + ").");
        } else {
            for (Participant p : masterMap.values()) {
                if (p.getAssigned() == null || !p.getAssigned().toLowerCase().startsWith("team-")) {
                    source.add(p);
                }
            }
            System.out.println("No prepared upload. Forming teams from all unassigned participants in master (" + source.size() + ").");
        }

        if (source.size() < 3) {
            System.out.println("Not enough participants to form teams.");
            return;
        }

        //team size should only be within 3 to 10,to form properly distributed teams
        int teamSize;
        while (true) {
            System.out.print("Enter desired team size (3..10): ");
            try {
                teamSize = Integer.parseInt(sc.nextLine().trim());
                LOGGER.info("Team formation initiated for team size: " + teamSize);
                if (teamSize >= 3 && teamSize <= 10) break;
            } catch (Exception ignored) {}
            System.out.println("Please enter a valid integer between 3 and 10.");
        }

        //sq number 1.1 in form teams use case of organizer
        int startId = calculateNextTeamId();
        //using threads to perform tasks parallelly.
        //sq number 1.2 in form teams use case of organizer
        SurveyProcessor sp = new SurveyProcessor(source, classifier);
        //sq number 1.3 in form teams use case of organizer
        TeamFormationWorker tfw = new TeamFormationWorker(source, teamSize, startId);

        Thread t1 = new Thread(sp, "SurveyProcessor");
        Thread t2 = new Thread(tfw, "TeamFormationWorker");

        t1.start();
        try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        t2.start();

        try {
            t1.join();
            t2.join();

        } catch (InterruptedException e) {
            System.out.println("Operation interrupted: " + e.getMessage());
        }

        currentTeams = tfw.getTeams();
        LOGGER.info("Team formation completed. Teams: " + currentTeams.size());
        currentLeftovers = tfw.getLeftovers();
        System.out.println("Teams formed in memory: " + currentTeams.size() + " | Leftovers: " + currentLeftovers.size());
    }

    // export teams to make sure they are stored
    // unless formed teams would be disappeared since they are stored in memory
    //sq number 1 in export formed teams use case of organizer
    private void exportFormedTeams() {
        if (currentTeams == null || currentTeams.isEmpty()) {
            System.out.println("No teams to export. Please form teams first (option 3).");
            return;
        }

        if (lastUploadedRows != null && !lastUploadedRows.isEmpty()) {
            //sq number 1.1 in export formed teams use case of organizer
            masterMap = fm.mergeUploadedIntoMasterAtExport(lastUploadedRows);
        } else {
            //sq number 1.2 in export formed teams use case of organizer
            masterMap = fm.readMasterMap();
        }

        // Remove empty teams ONLY. Do NOT re-index/renumber them.
        List<Team> nonEmptyTeams = new ArrayList<>();
        for (Team t : currentTeams) {
            //sq number 1.3 in export formed teams use case of organizer
            if (t.size() > 0) {
                nonEmptyTeams.add(t);
            }
        }
        currentTeams = nonEmptyTeams;

        // Apply assignments
        // Now, t.getTeamID() will retain the correct ID (e.g., 6, 7) from the Builder
        for (Team t : currentTeams) {
            for (Participant member : t.getMembers()) {
                Participant orig = masterMap.get(member.getId());

                String teamName = "Team-" + t.getTeamID(); // Uses correct ID

                if (orig != null) {
                    orig.setAssigned(teamName);
                } else {
                    member.setAssigned(teamName);
                    masterMap.put(member.getId(), member);
                }
            }
        }

        for (Participant p : currentLeftovers) {
            if (!masterMap.containsKey(p.getId())) masterMap.put(p.getId(), p);
            else masterMap.get(p.getId()).setAssigned("Unassigned");
        }

        //sq number 1.4 in export formed teams use case of organizer
        fm.writeMasterFromMap(masterMap);

        if (lastUploadedFilePath != null && lastUploadedRows != null) {
            //sq number 1.5 in export formed teams use case of organizer
            fm.updateUploadedFileWithAssignments(lastUploadedFilePath, lastUploadedRows, masterMap);
        }

        String fname = fm.exportTeams(currentTeams, "formed_teams");
        if (fname != null) {
            System.out.println("Export successful: " + fname);
            LOGGER.info("Teams exported successfully to file: " + fname);
        } else {
            LOGGER.warning("Team export failed.");
            System.out.println("Export failed.");
        }

        // Clean up
        preparedCandidates.clear();
        lastUploadedFilePath = null;
        lastUploadedRows = null;
        currentLeftovers.clear();
        currentTeams.clear();
    }

    //sq number 1 in view formed teams use case of organizer
    private void viewTeams() {
        if (currentTeams == null || currentTeams.isEmpty()) {
            System.out.println("No teams in memory.");
            return;
        }
        //sq number 1.1 in view formed teams use case of organizer
        for (Team t : currentTeams) t.displayTeam();
    }

    //search for partciapnts
    //sq number 1 in search participants use case of organizer
    private void searchParticipant() {
        System.out.print("Enter ID or Name or Email to search: ");
        String q = sc.nextLine().trim().toLowerCase();

        //sq number 1.1 in search participants use case of organizer
        masterMap = fm.readMasterMap();

        List<Participant> matches = new ArrayList<>();
        for (Participant p : masterMap.values()) {
            boolean matchId = (p.getId() != null && p.getId().toLowerCase().contains(q));
            boolean matchName = (p.getName() != null && p.getName().toLowerCase().contains(q));
            boolean matchEmail = (p.getEmail() != null && p.getEmail().toLowerCase().contains(q));

            if (matchId || matchName || matchEmail) {
                matches.add(p);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No matches found in master.");
            return;
        }

        //sq number 1.2 in search participants use case of organizer
        if (matches.size() == 1) {
            System.out.println("Found: " + matches.get(0).toString());
            return;
        }

        System.out.println("Multiple matches (" + matches.size() + "). Enter exact name to disambiguate:");
        String name = sc.nextLine().trim().toLowerCase();

        // Disambiguate by name (Loop)
        List<Participant> nm = new ArrayList<>();
        for (Participant p : matches) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                nm.add(p);
            }
        }

        //sq number 1.3 in search participants use case of organizer
        if (nm.size() == 1) {
            System.out.println("Found: " + nm.get(0).toString());
            return;
        }

        System.out.println("Still ambiguous. Enter exact email:");
        String email = sc.nextLine().trim().toLowerCase();

        // Disambiguate by email (Loop)
        List<Participant> em = new ArrayList<>();
        for (Participant p : matches) {
            if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(email)) {
                em.add(p);
            }
        }

        //sq number 1.4 in search participants use case of organizer
        if (em.size() == 1) System.out.println("Found: " + em.get(0).toString());
        else System.out.println("Could not uniquely identify participant. Please refine your query.");
    }

    //sq number 1 in reset tournament use case of organizer
    private void resetTournament() {
        System.out.println("\nWARNING: This will clear ALL team assignments from the Master File.");
        System.out.println("This is used to start a NEW tournament.");
        System.out.print("Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            // Clear memory logic
            currentTeams.clear();
            currentLeftovers.clear();
            preparedCandidates.clear();

            // Clear file logic
            //sq number 1.1 in reset tournament use case of organizer
            fm.resetAllAssignments(masterMap);

            System.out.println("Tournament data has been reset. You can now form new teams.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }
    //helper method
    private int calculateNextTeamId() {
        int maxId = 0;
        // Loop through master map to find highest Team-X
        for (Participant p : masterMap.values()) {
            String assigned = p.getAssigned();
            if (assigned != null && assigned.startsWith("Team-")) {
                try {
                    String numPart = assigned.replace("Team-", "");
                    int num = Integer.parseInt(numPart);
                    if (num > maxId) maxId = num;
                } catch (Exception ignored) {}
            }
        }
        return maxId + 1; // Start at next number
    }
}