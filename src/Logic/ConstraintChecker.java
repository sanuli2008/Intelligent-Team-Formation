package Logic;

import Model.Participant;
import Model.Team;

import java.util.HashMap;
import java.util.Map;

// * check for hard constraints.
// * this is used to ensure about matching criteria
// * this also validates the formed teams, checking if a team has 1 leader, 1-2 thinkers and balanced as available

public class ConstraintChecker {
    private final int gameCap = 2;

    public boolean canAddToTeam(Team t, Participant p, int teamSize) {
        // 1. Full Check
        if (t.size() >= teamSize) return false;

        // 2. duplicate check (Loop)
        // this loop through the team. If this find an ID that matches, then stops.
        for (Participant member : t.getMembers()) {
            if (member.getId().equalsIgnoreCase(p.getId())) {
                return false;
            }
        }

        // 3. Strict Leader Check
        if ("Leader".equalsIgnoreCase(p.getPersonalityType())) {
            if (leaderCount(t) >= 1) return false;
        }

        // 4. Strict Thinker Check
        if ("Thinker".equalsIgnoreCase(p.getPersonalityType())) {
            if (thinkerCount(t) >= 2) return false;
        }

        // 5. Game Cap Check (Loop)
        if (p.getGame() != null) {
            int count = 0;
            for (Participant member : t.getMembers()) {
                if (member.getGame() != null && member.getGame().equalsIgnoreCase(p.getGame())) {
                    count++;
                }
            }
            if (count >= gameCap) return false;
        }

        return true;
    }

    // --- Helper Methods (Written with Loops) ---

    public int leaderCount(Team t) {
        int count = 0;
        for (Participant m : t.getMembers()) {
            if ("Leader".equalsIgnoreCase(m.getPersonalityType())) {
                count++;
            }
        }
        return count;
    }

    public int thinkerCount(Team t) {
        int count = 0;
        for (Participant m : t.getMembers()) {
            if ("Thinker".equalsIgnoreCase(m.getPersonalityType())) {
                count++;
            }
        }
        return count;
    }

    public boolean validateFinalTeam(Team t, int teamSize) {
        if (t.size() != teamSize) return false;

        if (leaderCount(t) != 1) return false; // Strictly 1 Leader

        int thinkers = thinkerCount(t);
        if (thinkers < 1 || thinkers > 2) return false; // Strictly 1 or 2 Thinkers

        // Game Cap Check for whole team
        // We use a Map to count games manually
        Map<String, Integer> gameCounts = new HashMap<>();
        for (Participant m : t.getMembers()) {
            if (m.getGame() != null) {
                String g = m.getGame().toLowerCase();
                // If exists, increment; else set to 1
                gameCounts.put(g, gameCounts.getOrDefault(g, 0) + 1);
            }
        }
        // Check if any count exceeds cap(2)
        for (int count : gameCounts.values()) {
            if (count > gameCap) return false;
        }

        return true;
    }
}