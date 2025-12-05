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
    //sq number 1.6.1 in export formed teams use case of organizer
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
        //sq number 1.1.1 in view formed teams use case of organizer
        System.out.println("\n=== Team-" + teamID + " (Avg skill: " + String.format("%.2f", averageSkill()) + ") ===");
        for (Participant p : members) {
            System.out.println(" - " + p.getId() + " | " + p.getName() + " | Role: " + p.getRole()
                    + " | Game: " + p.getGame() + " | Personality: " + p.getPersonalityType()
                    + " | Skill: " + p.getSkillLevel());
        }
    }
}
