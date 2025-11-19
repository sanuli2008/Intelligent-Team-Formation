

//public class FileManager {
//
//    // === LOAD PARTICIPANTS FROM CSV ===
//    public List<Participant> loadCSV(String fileName) {
//        List<Participant> participants = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line = br.readLine(); // skip header
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) continue;
//                String[] data = line.split(",");
//
//                // CSV column order:
//                // ID, Name, Email, PreferredGame, SkillLevel, PreferredRole, PersonalityScore, PersonalityType
//                String id = data[0].trim();
//                String name = data[1].trim();
//                String email = data[2].trim();
//                String game = data[3].trim();
//                int skill = Integer.parseInt(data[4].trim());
//                String role = data[5].trim();
//                int personalityScore = Integer.parseInt(data[6].trim());
//                String personalityType = data.length > 7 ? data[7].trim() : "";
//
//                participants.add(new Participant(id, name, email, game, skill, role, personalityScore, personalityType));
//            }
//            System.out.println("✅ Loaded " + participants.size() + " participants from " + fileName);
//        } catch (Exception e) {
//            System.out.println("❌ Error reading CSV: " + e.getMessage());
//        }
//
//        return participants;
//    }
//
//    // === SAVE FORMED TEAMS TO CSV ===
//    public void saveTeamsCSV(List<Team> teams, String fileName) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
//            bw.write("TeamID,ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
//            bw.newLine();
//
//            for (Team t : teams) {
//                for (Participant p : t.getMembers()) {
//                    bw.write(t.getTeamID() + "," +
//                            p.getId() + "," +
//                            p.getName() + "," +
//                            p.getEmail() + "," +
//                            p.getGame() + "," +
//                            p.getSkillLevel() + "," +
//                            p.getRole() + "," +
//                            p.getPersonalityScore() + "," +
//                            p.getPersonalityType());
//                    bw.newLine();
//                }
//            }
//
//            System.out.println("✅ Teams saved to " + fileName);
//        } catch (IOException e) {
//            System.out.println("❌ Error writing file: " + e.getMessage());
//        }
//    }
//
//    // === SAVE REMAINING PARTICIPANTS (LEFTOVERS) TO CSV ===
//    public void saveParticipantsCSV(List<Participant> participants, String fileName) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
//            bw.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
//            bw.newLine();
//
//            for (Participant p : participants) {
//                bw.write(p.getId() + "," +
//                        p.getName() + "," +
//                        p.getEmail() + "," +
//                        p.getGame() + "," +
//                        p.getSkillLevel() + "," +
//                        p.getRole() + "," +
//                        p.getPersonalityScore() + "," +
//                        p.getPersonalityType());
//                bw.newLine();
//            }
//
//            System.out.println("✅ Remaining participants saved to " + fileName);
//        } catch (IOException e) {
//            System.out.println("❌ Error writing remaining participants: " + e.getMessage());
//        }
//    }
//
//    // === APPEND NEW PARTICIPANT TO CSV ===
//    public String appendParticipant(String fileName, Participant p) {
//        String nextID = getNextID(fileName);
//        p.setId(nextID);
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
//            bw.write(p.getId() + "," +
//                    p.getName() + "," +
//                    p.getEmail() + "," +
//                    p.getGame() + "," +
//                    p.getSkillLevel() + "," +
//                    p.getRole() + "," +
//                    p.getPersonalityScore() + "," +
//                    p.getPersonalityType());
//            bw.newLine();
//            System.out.println("✅ Participant appended with ID: " + p.getId());
//        } catch (IOException e) {
//            System.out.println("❌ Error writing participant: " + e.getMessage());
//        }
//        return nextID;
//    }
//
//
//    // === GET NEXT PARTICIPANT ID ===
//    public String getNextID(String fileName) {
//        String lastID = "P000";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            br.readLine(); // skip header
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) continue;
//                String[] data = line.split(",");
//                lastID = data[0].trim();
//            }
//        } catch (IOException e) {
//            System.out.println("⚠️ Could not read file for ID generation: " + e.getMessage());
//        }
//
//        int num = 0;
//        try {
//            num = Integer.parseInt(lastID.substring(1)); // e.g. P003 -> 3
//        } catch (Exception e) {
//            num = 0;
//        }
//
//        num++;
//        return String.format("P%03d", num);
//    }
//}
package Logic;
import Model.Participant;
import Model.Team;

import java.io.*;
import java.util.*;

public class FileManager {

    public static final String MEMBERS_HEADER = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,Assigned";

    // Load participants; invalid rows (missing score/type) are flagged in returned map: valid list + invalid list
    public Map<String, List<Participant>> loadCSVWithValidation(String fileName) {
        List<Participant> valid = new ArrayList<>();
        List<Participant> invalid = new ArrayList<>();
        File f = new File(fileName);
        if (!f.exists()) {
            // no file -> empty lists
            Map<String, List<Participant>> result = new HashMap<>();
            result.put("valid", valid);
            result.put("invalid", invalid);
            return result;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                // ensure length at least 9
                while (parts.length < 9) parts = Arrays.copyOf(parts, 9);
                String id = safe(parts,0);
                String name = safe(parts,1);
                String email = safe(parts,2);
                String game = safe(parts,3);
                int skill = parseIntSafe(safe(parts,4), 0);
                String role = safe(parts,5);
                int pscore = parseIntSafe(safe(parts,6), 0);
                String ptype = safe(parts,7);
                String assigned = safe(parts,8);
                Participant p = new Participant(id, name, email, game, skill, role, pscore, ptype, assigned);
                // validation: require score>0 and ptype non-empty plus other required fields
                boolean rowValid = !id.isEmpty() && !name.isEmpty() && !email.isEmpty() && !game.isEmpty()
                        && skill >= 1 && skill <= 10 && !role.isEmpty() && pscore > 0 && !ptype.isEmpty();
                if (rowValid) valid.add(p); else invalid.add(p);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        Map<String, List<Participant>> result = new HashMap<>();
        result.put("valid", valid);
        result.put("invalid", invalid);
        return result;
    }

    // append (participant should have no id yet); returns assigned id
    public String appendParticipant(String fileName, Participant p) {
        String nextID = getNextID(fileName);
        p.setId(nextID);
        boolean exists = new File(fileName).exists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            if (!exists) {
                bw.write(MEMBERS_HEADER);
                bw.newLine();
            }
            bw.write(csvLineFromParticipant(p));
            bw.newLine();
            System.out.println("✅ Participant appended with ID " + p.getId());
        } catch (IOException e) {
            System.out.println("Error appending participant: " + e.getMessage());
        }
        return nextID;
    }

    // overwrite members.csv with updated participants (used to update Assigned field)
    public void overwriteMembersCSV(String fileName, List<Participant> participants) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(MEMBERS_HEADER);
            bw.newLine();
            for (Participant p : participants) {
                bw.write(csvLineFromParticipant(p));
                bw.newLine();
            }
            System.out.println("✅ members.csv updated (" + participants.size() + " rows).");
        } catch (IOException e) {
            System.out.println("Error writing members CSV: " + e.getMessage());
        }
    }

    // save formed teams to csv
    public void saveTeamsCSV(List<Team> teams, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("TeamID,ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            bw.newLine();
            for (Team t : teams) {
                for (Participant p : t.getMembers()) {
                    bw.write(String.join(",",
                            String.valueOf(t.getTeamID()),
                            safeString(p.getId()),
                            safeString(p.getName()),
                            safeString(p.getEmail()),
                            safeString(p.getGame()),
                            String.valueOf(p.getSkillLevel()),
                            safeString(p.getRole()),
                            String.valueOf(p.getPersonalityScore()),
                            safeString(p.getPersonalityType())
                    ));
                    bw.newLine();
                }
            }
            System.out.println("✅ formed_teams.csv saved.");
        } catch (IOException e) {
            System.out.println("Error saving teams CSV: " + e.getMessage());
        }
    }

    // helper: generate next P### id
    public String getNextID(String fileName) {
        String last = "P000";
        File f = new File(fileName);
        if (!f.exists()) return "P001";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && !parts[0].trim().isEmpty()) last = parts[0].trim();
            }
        } catch (IOException e) {
            System.out.println("Warning: couldn't read file for ID generation: " + e.getMessage());
        }
        int n = 0;
        try { n = Integer.parseInt(last.replaceAll("[^0-9]", "")); } catch (Exception ex) { n = 0; }
        n++;
        return String.format("P%03d", n);
    }

    // helpers
    private String csvLineFromParticipant(Participant p) {
        return String.join(",",
                safeString(p.getId()),
                safeString(p.getName()),
                safeString(p.getEmail()),
                safeString(p.getGame()),
                String.valueOf(p.getSkillLevel()),
                safeString(p.getRole()),
                String.valueOf(p.getPersonalityScore()),
                safeString(p.getPersonalityType()),
                safeString(p.getAssigned())
        );
    }

    private String safe(String[] a, int i) { if (a==null || i<0 || i>=a.length) return ""; return a[i]==null? "": a[i].trim(); }
    private String safeString(String s) { return s == null ? "" : s.replaceAll(",", ""); }
    private int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}

//import java.io.*;
//import java.util.*;
//import Model.Participant;
//import Model.Team;
//import java.io.*;
//import java.util.*;
//
//public class FileManager {
//
//    // header used in members.csv
//    public static final String MEMBERS_HEADER = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType";
//
//    // load participants from CSV (expects header as above)
//    public List<Participant> loadCSV(String fileName) {
//        List<Participant> participants = new ArrayList<>();
//        File f = new File(fileName);
//        if (!f.exists()) {
//            System.out.println("members file not found: " + fileName + " (create with header first).");
//            return participants;
//        }
//
//        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
//            String header = br.readLine(); // skip header
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) continue;
//                // split but keep it simple - no quoted commas handling
//                String[] parts = line.split(",", -1);
//                // ensure at least 8 columns
//                while (parts.length < 8) {
//                    parts = Arrays.copyOf(parts, 8);
//                }
//                String id = safe(parts,0);
//                String name = safe(parts,1);
//                String email = safe(parts,2);
//                String game = safe(parts,3);
//                int skill = parseIntSafe(safe(parts,4), 0);
//                String role = safe(parts,5);
//                int pscore = parseIntSafe(safe(parts,6), 0);
//                String ptype = safe(parts,7);
//
//                participants.add(new Participant(id, name, email, game, skill, role, pscore, ptype));
//            }
//        } catch (IOException e) {
//            System.out.println("Error reading CSV: " + e.getMessage());
//        }
//        return participants;
//    }
//
//    // overwrite members.csv with updated list (used when organizer recalculates missing fields)
//    public void overwriteMembersCSV(String fileName, List<Participant> participants) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
//            bw.write(MEMBERS_HEADER);
//            bw.newLine();
//            for (Participant p : participants) {
//                bw.write(String.join(",",
//                        safeString(p.getId()),
//                        safeString(p.getName()),
//                        safeString(p.getEmail()),
//                        safeString(p.getGame()),
//                        String.valueOf(p.getSkillLevel()),
//                        safeString(p.getRole()),
//                        String.valueOf(p.getPersonalityScore()),
//                        safeString(p.getPersonalityType())
//                ));
//                bw.newLine();
//            }
//            System.out.println("✅ Updated members.csv with computed fields.");
//        } catch (IOException e) {
//            System.out.println("Error writing members CSV: " + e.getMessage());
//        }
//    }
//
//    // append participant to CSV; sets and returns new ID
//    public String appendParticipant(String fileName, Participant p) {
//        String nextID = getNextID(fileName);
//        p.setId(nextID);
//        boolean fileExists = new File(fileName).exists();
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
//            if (!fileExists) {
//                bw.write(MEMBERS_HEADER);
//                bw.newLine();
//            }
//            bw.write(String.join(",",
//                    safeString(p.getId()),
//                    safeString(p.getName()),
//                    safeString(p.getEmail()),
//                    safeString(p.getGame()),
//                    String.valueOf(p.getSkillLevel()),
//                    safeString(p.getRole()),
//                    String.valueOf(p.getPersonalityScore()),
//                    safeString(p.getPersonalityType())
//            ));
//            bw.newLine();
//            System.out.println("✅ Participant appended with ID " + p.getId());
//        } catch (IOException e) {
//            System.out.println("Error appending participant: " + e.getMessage());
//        }
//        return nextID;
//    }
//
//    // get next ID by reading last non-empty ID in file
//    public String getNextID(String fileName) {
//        String last = "P000";
//        File f = new File(fileName);
//        if (!f.exists()) return "P001";
//        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
//            String header = br.readLine();
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) continue;
//                String[] parts = line.split(",", -1);
//                if (parts.length > 0 && !parts[0].trim().isEmpty()) last = parts[0].trim();
//            }
//        } catch (IOException e) {
//            System.out.println("Warning: couldn't read file for ID generation: " + e.getMessage());
//        }
//        int n = 0;
//        try { n = Integer.parseInt(last.substring(1)); } catch (Exception ex) { n = 0; }
//        n++;
//        return String.format("P%03d", n);
//    }
//
//    // save formed teams to CSV
//    public void saveTeamsCSV(List<Team> teams, String fileName) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
//            bw.write("TeamID,ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
//            bw.newLine();
//            for (Team t : teams) {
//                for (Participant p : t.getMembers()) {
//                    bw.write(String.join(",",
//                            String.valueOf(t.getTeamID()),
//                            safeString(p.getId()),
//                            safeString(p.getName()),
//                            safeString(p.getEmail()),
//                            safeString(p.getGame()),
//                            String.valueOf(p.getSkillLevel()),
//                            safeString(p.getRole()),
//                            String.valueOf(p.getPersonalityScore()),
//                            safeString(p.getPersonalityType())
//                    ));
//                    bw.newLine();
//                }
//            }
//            System.out.println("✅ Teams saved to " + fileName);
//        } catch (IOException e) {
//            System.out.println("Error saving teams CSV: " + e.getMessage());
//        }
//    }
//
//    // save leftover participants to CSV
//    public void saveParticipantsCSV(List<Participant> participants, String fileName) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
//            bw.write(MEMBERS_HEADER);
//            bw.newLine();
//            for (Participant p : participants) {
//                bw.write(String.join(",",
//                        safeString(p.getId()),
//                        safeString(p.getName()),
//                        safeString(p.getEmail()),
//                        safeString(p.getGame()),
//                        String.valueOf(p.getSkillLevel()),
//                        safeString(p.getRole()),
//                        String.valueOf(p.getPersonalityScore()),
//                        safeString(p.getPersonalityType())
//                ));
//                bw.newLine();
//            }
//            System.out.println("✅ Remaining participants saved to " + fileName);
//        } catch (IOException e) {
//            System.out.println("Error saving remaining participants: " + e.getMessage());
//        }
//    }
//
//    // helpers
//    private String safe(String[] arr,int idx) {
//        if (arr == null || idx < 0 || idx >= arr.length) return "";
//        return arr[idx] == null ? "" : arr[idx].trim();
//    }
//    private String safeString(String s) { return s == null ? "" : s.replaceAll(",", ""); }
//    private int parseIntSafe(String s, int def) {
//        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
//    }
//}
