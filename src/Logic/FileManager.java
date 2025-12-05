package Logic;

import Exceptions.InvalidDataException;
import Exceptions.ValidationException;
import Model.Participant;
import Model.Team;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {

    private static final String MASTER_PATH = "src/all_participants.csv";
    private static final String HEADER = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,Assigned";
    // 1. Create a Logger instance for this class
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    public void ensureMasterExists() {
        File f = new File(MASTER_PATH);
        if (!f.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(HEADER);
                bw.newLine();
                System.out.println("[FileManager] Created master file: " + MASTER_PATH);
                // LOGGING SUCCESS
                LOGGER.info("Created new master file at " + MASTER_PATH);
                System.out.println("[FileManager] Created master file: " + MASTER_PATH);
            } catch (IOException e) {
                System.out.println("[FileManager] Error creating master: " + e.getMessage());
                // LOGGING ERROR
                LOGGER.log(Level.SEVERE, "Error creating master file", e);
                System.out.println("[FileManager] Error creating master: " + e.getMessage());
            }
        }
    }

    public Map<String, Participant> readMasterMap() {
        //used hash map, because containsKey can easily check for duplicates
        Map<String, Participant> map = new LinkedHashMap<>();
        File f = new File(MASTER_PATH);
        if (!f.exists()) return map;
        int corruptLineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                try {
                    // This method now throws an exception, so we must catch it
                    Participant p = parseLineToParticipant(line);
                    map.put(p.getId(), p);

                } catch (InvalidDataException e) {
                    // Log the error and skip this specific bad line
                    LOGGER.warning("Skipping corrupt line in master file: " + e.getMessage());
                    System.out.println("[FileManager] Warning - Skipping corrupt line in master file: " + e.getMessage());
                    corruptLineCount++;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read master file", e);
            System.out.println("[FileManager] Error reading master: " + e.getMessage());
        }
        if (corruptLineCount > 0) {
            System.out.println("[FileManager] Warning: Skipped " + corruptLineCount + " invalid entries during master load.");
        }
        return map;
    }

    //writing the participants to master file after forming teams
    public void writeMasterFromMap(Map<String, Participant> map) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MASTER_PATH))) {
            bw.write(HEADER);
            bw.newLine();
            for (Participant p : map.values()) {
                bw.write(csvFromParticipant(p));
                bw.newLine();
            }
            System.out.println("[FileManager] Master saved: " + MASTER_PATH);
        } catch (IOException e) {
            System.out.println("[FileManager] Error writing master: " + e.getMessage());
        }
    }

    //appending newly registered participant to the master file
    public String appendNewParticipant(Participant p) {
        ensureMasterExists();
        Map<String, Participant> master = readMasterMap();

        // 1. Enforce ID existence and uniqueness (as requested)
        if (p.getId() == null || p.getId().isEmpty()) {
            System.out.println("[FileManager] Error: Cannot append participant. ID is missing.");
            return null;
        }
        if (master.containsKey(p.getId())) {
            // This case should be caught by UI, but serves as a final safeguard
            System.out.println("[FileManager] Error: Cannot append participant. ID already exists: " + p.getId());
            return null;
        }
        if (p.getAssigned() == null || p.getAssigned().isEmpty()) p.setAssigned("Unassigned");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MASTER_PATH, true))) {
            bw.write(csvFromParticipant(p));
            bw.newLine();
            System.out.println("[FileManager] Appended participant: " + p.getName() + " (" + p.getId() + ")");
            return p.getId();
        } catch (IOException e) {
            System.out.println("[FileManager] Error appending participant: " + e.getMessage());
            return null;
        }
    }

    // checks if a file has the "Assigned" column and for duplicate IDs to prevent uploading already uploaded file
    public List<Participant> importCandidates(String path, Map<String, Participant> masterMap) throws IOException, InvalidDataException {
        File f = new File(path);
        if (!f.exists()) throw new IOException("File not found: " + path);

        List<Participant> validCandidates = new ArrayList<>();
        int duplicates = 0;
        int invalidCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            // check for assigned column
            String header = br.readLine();
            if (header != null && header.toLowerCase().contains("assigned")) {
                throw new InvalidDataException("File contains 'Assigned' column. Re-importing assigned files is not allowed.");
            }

            // check for duplicates
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                try {
                    Participant p = parseLineToParticipant(line);

                    // If the ID exists in master, we skip it here.
                    if (p.getId() != null && !p.getId().isEmpty() && masterMap.containsKey(p.getId())) {
                        duplicates++;
                    } else {
                        // It is unique. Add it.
                        p.setAssigned("Unassigned");
                        validCandidates.add(p);
                    }
                } catch (InvalidDataException e) {
                    LOGGER.warning("Import skipped invalid line: " + e.getMessage());
                    invalidCount++;
                }
            }
        }

        if (duplicates > 0) {
            System.out.println("[FileManager] Skipped " + duplicates + " participants (IDs already in Master).");
        }
        if (invalidCount > 0) {
            System.out.println("[FileManager] Skipped " + invalidCount + " invalid rows (Missing values/Bad Format).");
        }

        return validCandidates;
    }

    // this method helps in validating personality score and skill score in ploaded file
    private Participant parseLineToParticipant(String line) throws InvalidDataException {
        String[] cols = line.split(",", -1);
        if (cols.length < 8) {
            throw new InvalidDataException("Incomplete data row.");
        }
        while (cols.length < 9) cols = Arrays.copyOf(cols, 9);
        for (int i = 0; i < 9; i++) if (cols[i] == null) cols[i] = "";

        String id = cols[0].trim();
        String name = cols[1].trim();
        String email = cols[2].trim();
        String game = cols[3].trim();
        String role = cols[5].trim();
        String ptype = cols[7].trim();
        String assignedRaw = cols[8].trim();

        // VALIDATION LOGIC
        // Mandatory format checks delegated to ValidationService
        try {
            ValidationService.validateIdFormat(id); // Checks P### format and non-empty
            ValidationService.validateName(name);
            ValidationService.validateEmail(email);
            // We skip game/role validation here, as they are often flexible in CSVs unless strictly defined.
        } catch (ValidationException e) {
            // Re-throw as InvalidDataException to ensure consistency in FileManager catch blocks
            // Although we can just rethrow ValidationException, InvalidDataException is used for File issues.
            // However, here we re-throw the ValidationException (Runtime) which is caught in readMasterMap/importCandidates.
            throw e;
        }
        int skill;
        int pscore;

        try {
            skill = Integer.parseInt(cols[4].trim());
            pscore = Integer.parseInt(cols[6].trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Skill or Score is not a number for: " + name);
        }

        // Range Validation delegated to ValidationService
        ValidationService.validateSkillLevel(skill);
        ValidationService.validatePersonalityScore(pscore);

        return new Participant(id, name, email, game, skill, role, pscore, ptype, interpretAssigned(assignedRaw));
    }

    public Map<String, Participant> mergeUploadedIntoMasterAtExport(List<Participant> uploaded) {
        ensureMasterExists();
        Map<String, Participant> master = readMasterMap();
        Map<String, String> emailToId = new HashMap<>();
        int skippedCount = 0;
        for (Participant p : master.values()) {
            if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                emailToId.put(p.getEmail().toLowerCase(), p.getId());
            }
        }

        for (Participant up : uploaded) {
            String upId = up.getId() == null ? "" : up.getId().trim();
            String upEmail = up.getEmail() == null ? "" : up.getEmail().toLowerCase();

            if (!upId.isEmpty() && master.containsKey(upId)) {
                continue; // Skip existing ID
            } else if (!upEmail.isEmpty() && emailToId.containsKey(upEmail)) {
                // Update existing email
                String mid = emailToId.get(upEmail);
                Participant stored = master.get(mid);
                if (up.getPersonalityScore() > 0) stored.setPersonalityScore(up.getPersonalityScore());
                if (up.getPersonalityType() != null && !up.getPersonalityType().isEmpty())
                    stored.setPersonalityType(up.getPersonalityType());
            } else {
                // New participant
                if (upId != null && !upId.isEmpty()) {
                    // Check if ID is in the correct format before adding it as new (optional but safer)
                    if (!upId.matches("(?i)P\\d{3}")) {
                        // Skip if invalid format
                        LOGGER.warning("[Merge Export] Skipping new participant due to invalid ID format: " + upId);
                        skippedCount++;
                        continue;
                    }

                    // Add as a truly new participant
                    up.setAssigned("Unassigned");
                    master.put(upId, up);
                    emailToId.put(upEmail, upId);
                } else {
                    //Instead of generating ID, we skip the entry.
                    LOGGER.warning("[Merge Export] Skipping new participant due to invalid ID format: " + upId);
                    skippedCount++;
                }
            }
        }
        if (skippedCount > 0) {
            System.out.println("[FileManager Merge] Warning: Skipped " + skippedCount + " new entries during merge (Missing/Invalid ID). Check log for details.");
        }
        return master;
    }

    public String exportTeams(List<Team> teams, String prefix) {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = prefix + "_" + ts + ".csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname))) {
            bw.write("TeamID,ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            bw.newLine();
            for (Team t : teams) {
                for (Participant p : t.getMembers()) {
                    bw.write(String.join(",",
                            "Team-" + t.getTeamID(),
                            safe(p.getId()), safe(p.getName()), safe(p.getEmail()), safe(p.getGame()),
                            String.valueOf(p.getSkillLevel()), safe(p.getRole()),
                            String.valueOf(p.getPersonalityScore()), safe(p.getPersonalityType())));
                    bw.newLine();
                }
            }
            System.out.println("[FileManager] Exported teams to " + fname);
            return fname;
        } catch (IOException e) {
            System.out.println("[FileManager] Error exporting teams: " + e.getMessage());
            return null;
        }
    }

    //to update the uploaded file with assigned team or else with unassigned
    public void updateUploadedFileWithAssignments(String uploadedPath, List<Participant> uploadedRows, Map<String, Participant> masterMap) {
        if (uploadedPath == null || uploadedPath.isEmpty() || uploadedRows == null) return;
        File f = new File(uploadedPath);
        if (!f.exists()) return;

        Map<String, String> idToAssigned = new HashMap<>();
        Map<String, String> emailToAssigned = new HashMap<>();

        for (Participant p : masterMap.values()) {
            if (p.getId() != null && !p.getId().isEmpty()) idToAssigned.put(p.getId(), p.getAssigned());
            if (p.getEmail() != null && !p.getEmail().isEmpty()) emailToAssigned.put(p.getEmail().toLowerCase(), p.getAssigned());
        }

        List<String> outLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            if (header == null) header = "";
            boolean hasAssigned = header.toLowerCase().contains("assigned");
            String newHeader = hasAssigned ? header : (header.isEmpty() ? HEADER : header + ",Assigned");
            outLines.add(newHeader);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { outLines.add(line); continue; }
                String[] cols = line.split(",", -1);
                if (cols.length < 9) cols = Arrays.copyOf(cols, 9);
                for (int i = 0; i < 9; i++) if (cols[i] == null) cols[i] = "";

                String id = cols[0].trim();
                String email = cols[2].trim().toLowerCase();
                String assigned = "Unassigned";

                if (!id.isEmpty() && idToAssigned.containsKey(id)) assigned = idToAssigned.get(id);
                else if (!email.isEmpty() && emailToAssigned.containsKey(email)) assigned = emailToAssigned.get(email);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    sb.append(cols[i] == null ? "" : cols[i]);
                    sb.append(",");
                }
                sb.append(assigned);
                outLines.add(sb.toString());
            }
        } catch (IOException e) {
            System.out.println("[FileManager] Error reading/updating uploaded file: " + e.getMessage());
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (String l : outLines) { bw.write(l); bw.newLine(); }
            System.out.println("[FileManager] Updated uploaded file with Assigned column: " + uploadedPath);
        } catch (IOException e) {
            System.out.println("[FileManager] Error writing uploaded file: " + e.getMessage());
        }
    }

    //this is used to reset the whole main
    //as of my scenario, once a new tournament is held the main file need to be unassigned
    //because, main file contains permanent members of the club
    public void resetAllAssignments(Map<String, Participant> masterMap) {
        // 1. Loop through everyone
        int count = 0;
        for (Participant p : masterMap.values()) {
            // Only reset if they are actually assigned
            if (p.getAssigned() != null && !p.getAssigned().equals("Unassigned")) {
                p.setAssigned("Unassigned");
                count++;
            }
        }

        // 2. Save back to file
        writeMasterFromMap(masterMap);

        // 3. Log it (since you have logging now)
        try {
            java.util.logging.Logger.getLogger(FileManager.class.getName())
                    .info("Tournament reset. Cleared assignments for " + count + " participants.");
        } catch (Exception ignored) {}

        System.out.println("[FileManager] Reset complete. " + count + " participants are now Unassigned.");
    }

    private String interpretAssigned(String raw) {
        if (raw == null) return "Unassigned";
        raw = raw.trim();
        if (raw.toLowerCase().startsWith("team-")) return raw;
        return "Unassigned";
    }

    private String generateNextPId(Map<String, Participant> master) {
        int max = 0;
        for (String id : master.keySet()) {
            if (id == null) continue;
            if (id.toUpperCase().startsWith("P")) {
                String digits = id.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    try { max = Math.max(max, Integer.parseInt(digits)); } catch (Exception ignored) {}
                }
            }
        }
        int next = max + 1;
        return String.format("P%03d", next);
    }

    private String csvFromParticipant(Participant p) {
        return String.join(",",
                safe(p.getId()), safe(p.getName()), safe(p.getEmail()), safe(p.getGame()),
                String.valueOf(p.getSkillLevel()), safe(p.getRole()), String.valueOf(p.getPersonalityScore()),
                safe(p.getPersonalityType()), safe(p.getAssigned()));
    }

    private int safeParseInt(String s, int d) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return d; }
    }

    private String safe(String s) { return s == null ? "" : s.replaceAll(",", ""); }
}