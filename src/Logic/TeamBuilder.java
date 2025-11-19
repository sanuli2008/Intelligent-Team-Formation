//package Logic;
//
//import Model.Participant;
//import Model.Team;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class TeamBuilder {
//
//    // Attributes
//    private int teamSize;
//    private List<Participant> participants;
//    private List<Team> teams;
//
//    // Constructor
//    public TeamBuilder(int teamSize, List<Participant> participants) {
//        this.teamSize = teamSize;
//        this.participants = participants;
//        this.teams = new ArrayList<>();
//    }
//
//    // Main method to form balanced teams
//    public void formTeams() {
//        if (participants == null || participants.isEmpty()) {
//            System.out.println("No participants available to form teams.");
//            return;
//        }
//
//        // Shuffle to add randomness and fairness
//        Collections.shuffle(participants);
//
//        // Sort by personality type or skill (optional)
//        Collections.sort(participants, Comparator.comparing(Participant::getPersonalityType));
//
//        int teamID = 1;
//        Team currentTeam = new Team(teamID);
//
//        for (Participant p : participants) {
//            currentTeam.addMember(p);
//            // When current team is full, create a new one
//            if (currentTeam.getMembers().size() == teamSize) {
//                teams.add(currentTeam);
//                teamID++;
//                currentTeam = new Team(teamID);
//            }
//        }
//
//        // Add the last partially filled team (if any)
//        if (!currentTeam.getMembers().isEmpty() && !teams.contains(currentTeam)) {
//            teams.add(currentTeam);
//        }
//
//        System.out.println("✅ Teams formed successfully: " + teams.size());
//    }
//
//    // Method to display all teams
//    public void displayAllTeams() {
//        for (Team t : teams) {
//            t.displayTeam();
//        }
//    }
//
//    // Getter
//    public List<Team> getTeams() {
//        return teams;
//    }
//}



//public class TeamBuilder {
//
//    private final int teamSize;
//    private final List<Participant> participants;
//    private final List<Team> teams = new ArrayList<>();
//    private final List<Participant> leftover = new ArrayList<>();
//
//    public TeamBuilder(int teamSize, List<Participant> participants) {
//        this.teamSize = teamSize;
//        this.participants = participants;
//    }
//
//    public void formTeams() {
//        if (participants == null || participants.isEmpty()) {
//            System.out.println("⚠️ No participants available.");
//            return;
//        }
//
//        // shuffle to randomize distribution
//        Collections.shuffle(participants);
//
//        int teamID = 1;
//        Team current = new Team(teamID);
//
//        for (Participant p : participants) {
//            current.addMember(p);
//            if (current.getMembers().size() == teamSize) {
//                teams.add(current);
//                teamID++;
//                current = new Team(teamID);
//            }
//        }
//
//        if (!current.getMembers().isEmpty() && current.getMembers().size() < teamSize) {
//            leftover.addAll(current.getMembers());
//        }
//
//        System.out.println("✅ Formed " + teams.size() + " full teams, with "
//                + leftover.size() + " participant(s) remaining.");
//    }
//
//    public void displayAllTeams() {
//        for (Team t : teams) {
//            t.displayTeam();
//        }
//    }
//
//    public List<Team> getTeams() { return teams; }
//    public List<Participant> getLeftoverParticipants() { return leftover; }
//}
package Logic;

import Model.Participant;
import Model.Team;
import java.util.*;

import java.util.*;
import java.util.*;

public class TeamBuilder {
    private final int teamSize;
    private final List<Participant> participants; // validated participants
    private final List<Team> teams = new ArrayList<>();
    private final List<Participant> leftover = new ArrayList<>();
    private final Random rnd = new Random();

    public TeamBuilder(int teamSize, List<Participant> participants) {
        this.teamSize = Math.max(1, teamSize);
        this.participants = new ArrayList<>(participants);
    }

    public void formTeams() {
        teams.clear();
        leftover.clear();
        if (participants.isEmpty()) {
            System.out.println("No participants to form teams.");
            return;
        }

        Collections.shuffle(participants, rnd);

        int fullTeamsCount = participants.size() / teamSize;
        if (fullTeamsCount == 0) {
            leftover.addAll(participants);
            System.out.println("Not enough participants to form a single full team. All marked Unassigned.");
            return;
        }

        for (int i = 1; i <= fullTeamsCount; i++) teams.add(new Team(i));

        // Partition by personality
        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();
        for (Participant p : participants) {
            if ("Leader".equalsIgnoreCase(p.getPersonalityType())) leaders.add(p);
            else if ("Thinker".equalsIgnoreCase(p.getPersonalityType())) thinkers.add(p);
            else balanced.add(p);
        }

        // 1) Assign one leader to each team where possible
        assignOnePerTeam(teams, leaders);

        // 2) Assign 1-2 thinkers per team (first pass assign 1 each)
        assignUpToPerTeam(teams, thinkers, 2, 1); // prefer 1, but limit 2

        // 3) Ensure role diversity: try to add participants whose role is missing in teams
        List<Participant> remaining = new ArrayList<>();
        remaining.addAll(balanced);
        remaining.addAll(leaders); // leftover leaders
        remaining.addAll(thinkers); // leftover thinkers

        // Sort remaining by skill descending for balanced distribution
        remaining.sort((a,b) -> Integer.compare(b.getSkillLevel(), a.getSkillLevel()));

        // fill teams trying to respect constraints: game cap (<=2), role diversity (prefer roles not present)
        int idx = 0;
        for (Participant p : remaining) {
            boolean placed = false;
            // attempt to place to a team that benefits role diversity and respects game cap
            for (int attempt = 0; attempt < teams.size() && !placed; attempt++) {
                Team t = teams.get((idx + attempt) % teams.size());
                if (canAddToTeam(t, p)) {
                    t.addMember(p);
                    placed = true;
                }
            }
            if (!placed) {
                // relaxed placement: add to the team with smallest size that still < teamSize
                Team best = teams.stream()
                        .filter(t -> t.getMembers().size() < teamSize)
                        .min(Comparator.comparingInt(t -> t.getMembers().size()))
                        .orElse(null);
                if (best != null) best.addMember(p);
                else leftover.add(p); // couldn't place anywhere
            }
            idx = (idx + 1) % teams.size();
        }

        // Trim teams to teamSize and move extras to leftover
        List<Participant> extras = new ArrayList<>();
        for (Team t : teams) {
            while (t.getMembers().size() > teamSize) {
                Participant removed = t.getMembers().remove(t.getMembers().size()-1);
                extras.add(removed);
            }
        }
        leftover.addAll(extras);

        // Mark assigned participants' Assigned field
        Set<Participant> assigned = new HashSet<>();
        for (Team t : teams) {
            for (Participant p : t.getMembers()) {
                p.setAssigned("Team-" + t.getTeamID());
                assigned.add(p);
            }
        }
        for (Participant p : participants) {
            if (!assigned.contains(p)) p.setAssigned("Unassigned");
        }

        System.out.println("Formed " + teams.size() + " teams. Leftovers: " + leftover.size());
    }

    // assign exactly one from pool to each team if possible
    private void assignOnePerTeam(List<Team> teams, List<Participant> pool) {
        Iterator<Participant> it = pool.iterator();
        int idx = 0;
        while (it.hasNext() && idx < teams.size()) {
            Participant p = it.next();
            Team t = teams.get(idx);
            if (t.getMembers().size() < teamSize && canAddToTeam(t,p)) {
                t.addMember(p);
                it.remove();
                idx++;
            } else {
                // try next pool or next team
                idx++;
            }
        }
    }

    // assign up to maxPerTeam but try to assign preferPerTeam first (uses round-robin)
    private void assignUpToPerTeam(List<Team> teams, List<Participant> pool, int maxPerTeam, int preferPerTeam) {
        if (pool.isEmpty()) return;
        // First pass: try to assign preferPerTeam to each team
        Iterator<Participant> it = pool.iterator();
        int teamIndex = 0;
        while (it.hasNext() && teamIndex < teams.size()) {
            Participant p = it.next();
            Team t = teams.get(teamIndex);
            long samePersonality = t.getMembers().stream().filter(m -> m.getPersonalityType().equalsIgnoreCase(p.getPersonalityType())).count();
            if (t.getMembers().size() < teamSize && samePersonality < preferPerTeam && canAddToTeam(t,p)) {
                t.addMember(p);
                it.remove();
            }
            teamIndex++;
            if (teamIndex >= teams.size()) teamIndex = 0;
        }
        // Second pass: try to fill up to maxPerTeam
        it = pool.iterator();
        teamIndex = 0;
        while (it.hasNext()) {
            Participant p = it.next();
            Team t = teams.get(teamIndex % teams.size());
            long samePersonality = t.getMembers().stream().filter(m -> m.getPersonalityType().equalsIgnoreCase(p.getPersonalityType())).count();
            if (t.getMembers().size() < teamSize && samePersonality < maxPerTeam && canAddToTeam(t,p)) {
                t.addMember(p);
                it.remove();
            }
            teamIndex++;
        }
    }

    // returns true if p can be added to t respecting constraints
    private boolean canAddToTeam(Team t, Participant p) {
        if (t.getMembers().size() >= teamSize) return false;
        // game cap <=2
        long sameGame = t.getMembers().stream().filter(m -> m.getGame().equalsIgnoreCase(p.getGame())).count();
        if (sameGame >= 2) return false;
        // role diversity: prefer adding different roles when team has < 3 distinct roles
        long distinctRoles = t.getMembers().stream().map(m -> m.getRole().toLowerCase()).distinct().count();
        boolean rolePresent = t.getMembers().stream().anyMatch(m -> m.getRole().equalsIgnoreCase(p.getRole()));
        if (distinctRoles < Math.min(3, teamSize - 1)) {
            return !rolePresent; // prefer different role
        }
        // else allow
        return true;
    }

    public List<Team> getTeams() { return teams; }
    public List<Participant> getLeftoverParticipants() { return leftover; }
    public void displayAllTeams() { for (Team t : teams) t.displayTeam(); }
}

//public class TeamBuilder {
//
//    private final int teamSize;
//    private final List<Participant> participants; // copy expected
//    private final List<Team> teams = new ArrayList<>();
//    private final List<Participant> leftover = new ArrayList<>();
//    private final Random rnd = new Random();
//
//    public TeamBuilder(int teamSize, List<Participant> participants) {
//        this.teamSize = Math.max(1, teamSize);
//        // make a shallow copy so caller's list unaffected
//        this.participants = new ArrayList<>(participants);
//    }
//
//    public void formTeams() {
//        if (participants.isEmpty()) {
//            System.out.println("No participants to form teams.");
//            return;
//        }
//
//        // Shuffle to add randomness
//        Collections.shuffle(participants, rnd);
//
//        // Strategy:
//        // 1) Group participants by personality and role to try to satisfy constraints.
//        // 2) Create initial empty teams count = participants.size() / teamSize
//        int fullTeamsCount = participants.size() / teamSize;
//        if (fullTeamsCount == 0) {
//            // no full teams; all are leftovers
//            leftover.addAll(participants);
//            System.out.println("No full teams possible. All participants are leftovers.");
//            return;
//        }
//
//        for (int i = 1; i <= fullTeamsCount; i++) teams.add(new Team(i));
//
//        // Build auxiliary lists by personality
//        List<Participant> leaders = new ArrayList<>();
//        List<Participant> thinkers = new ArrayList<>();
//        List<Participant> balanced = new ArrayList<>();
//
//        for (Participant p : participants) {
//            switch (p.getPersonalityType()) {
//                case "Leader": leaders.add(p); break;
//                case "Thinker": thinkers.add(p); break;
//                case "Balanced": balanced.add(p); break;
//                default: balanced.add(p); break;
//            }
//        }
//
//        // 1) Assign 1 leader per team where possible
//        assignByRoleWithConstraints(teams, leaders, 1);
//
//        // 2) Add 1-2 thinkers per team (first pass add 1)
//        assignByRoleWithConstraints(teams, thinkers, 1);
//
//        // 3) Fill remaining slots with balanced participants trying to satisfy role diversity and game cap
//        List<Participant> remaining = new ArrayList<>();
//        remaining.addAll(balanced);
//        // also include any leftover from leaders/thinkers not assigned
//        collectUnassigned(leaders, teams, remaining);
//        collectUnassigned(thinkers, teams, remaining);
//
//        // Sort remaining by skill descending to attempt balanced distribution
//        remaining.sort((a,b) -> Integer.compare(b.getSkillLevel(), a.getSkillLevel()));
//
//        // We'll iterate teams in round-robin and try to place participants attempting role diversity & game cap
//        int idx = 0;
//        for (Participant p : remaining) {
//            boolean placed = false;
//            int attempts = 0;
//            while (!placed && attempts < teams.size()) {
//                Team t = teams.get((idx + attempts) % teams.size());
//                if (canAddToTeam(t, p)) {
//                    t.addMember(p);
//                    placed = true;
//                }
//                attempts++;
//            }
//            if (!placed) {
//                // couldn't place respecting constraints, place in team with the fewest members
//                Team best = teams.stream().min(Comparator.comparingInt(x -> x.getMembers().size())).orElse(teams.get(0));
//                best.addMember(p);
//            }
//            idx = (idx + 1) % teams.size();
//        }
//
//        // After filling, determine leftovers (participants not in any team)
//        Set<Participant> assigned = new HashSet<>();
//        for (Team t : teams) assigned.addAll(t.getMembers());
//        for (Participant p : participants) {
//            if (!assigned.contains(p)) leftover.add(p);
//        }
//
//        // There may be teams with > teamSize because we didn't strictly enforce at each step for simplicity,
//        // so trim overfilled teams and send extras to leftover (rare with above logic)
//        List<Participant> extras = new ArrayList<>();
//        for (Team t : teams) {
//            while (t.getMembers().size() > teamSize) {
//                Participant rem = t.getMembers().remove(t.getMembers().size()-1);
//                extras.add(rem);
//            }
//        }
//        leftover.addAll(extras);
//
//        System.out.println("Formed " + teams.size() + " teams. Leftovers: " + leftover.size());
//    }
//
//    // helper to assign from a pool up to limitPerTeam per team
//    private void assignByRoleWithConstraints(List<Team> teams, List<Participant> pool, int limitPerTeam) {
//        if (pool.isEmpty()) return;
//        Iterator<Participant> it = pool.iterator();
//        int teamIndex = 0;
//        while (it.hasNext()) {
//            Participant p = it.next();
//            Team t = teams.get(teamIndex % teams.size());
//            // ensure we don't exceed teamSize and the per-team personality count
//            long existingSamePersonality = t.getMembers().stream().filter(m -> m.getPersonalityType().equals(p.getPersonalityType())).count();
//            if (t.getMembers().size() < teamSize && existingSamePersonality < limitPerTeam && canAddToTeam(t, p)) {
//                t.addMember(p);
//                it.remove();
//            }
//            teamIndex++;
//            // break safety
//            if (teamIndex > teams.size() * 10) break;
//        }
//    }
//
//    // can we add participant p to team t respecting:
//    // - teamSize (not exceed)
//    // - game cap (max 2 same game per team)
//    // - try to increase role diversity (prefer adding if role not present)
//    private boolean canAddToTeam(Team t, Participant p) {
//        if (t.getMembers().size() >= teamSize) return false;
//        // game cap
//        long sameGame = t.getMembers().stream().filter(m -> m.getGame().equalsIgnoreCase(p.getGame())).count();
//        if (sameGame >= 2) return false; // cap 2
//        // role diversity: prefer if role not present
//        boolean rolePresent = t.getMembers().stream().anyMatch(m -> m.getRole().equalsIgnoreCase(p.getRole()));
//        // if team has too few different roles, prefer to add different roles
//        long distinctRoles = t.getMembers().stream().map(m -> m.getRole().toLowerCase()).distinct().count();
//        if (distinctRoles < Math.min(3, teamSize-1)) {
//            // strongly prefer different role
//            return !rolePresent;
//        }
//        // otherwise allow
//        return true;
//    }
//
//    private void collectUnassigned(List<Participant> pool, List<Team> teams, List<Participant> remaining) {
//        // remove those who were assigned
//        Set<Participant> assigned = new HashSet<>();
//        for (Team t : teams) assigned.addAll(t.getMembers());
//        for (Participant p : new ArrayList<>(pool)) {
//            if (!assigned.contains(p)) {
//                remaining.add(p);
//            }
//        }
//    }
//
//    public List<Team> getTeams() { return teams; }
//    public List<Participant> getLeftoverParticipants() { return leftover; }
//
//    public void displayAllTeams() {
//        for (Team t : teams) t.displayTeam();
//    }
//}
