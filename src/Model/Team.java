package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Team - container for team members and helpers
 */
public class Team {
    private final int teamID;
    //used lists here, because they are dynamic allowing me to add/remove while balancing
    private final List<Participant> members = new ArrayList<>();

    public Team(int id) { this.teamID = id; }
    public int getTeamID() { return teamID; }
    public List<Participant> getMembers() { return members; }
    public void addMember(Participant p) { members.add(p); }
    public int size() { return members.size(); }

    public double averageSkill() {
        if (members.isEmpty()) return 0.0;
        double sum = 0;
        for (Participant p : members) sum += p.getSkillLevel();
        return sum / members.size();
    }

    public void displayTeam() {
        System.out.println("\n=== Team-" + teamID + " (Avg skill: " + String.format("%.2f", averageSkill()) + ") ===");
        for (Participant p : members) {
            System.out.println(" - " + p.getId() + " | " + p.getName() + " | Role: " + p.getRole()
                    + " | Game: " + p.getGame() + " | Personality: " + p.getPersonalityType()
                    + " | Skill: " + p.getSkillLevel());
        }
    }
}
