

//import java.util.ArrayList;
//import java.util.List;
//
//public class Team {
//    // Attributes
//    private int teamID;
//    private List<Participant> members;
//    private double averageSkill;
//
//    // Constructor
//    public Team(int teamID) {
//        this.teamID = teamID;
//        this.members = new ArrayList<>();
//        this.averageSkill = 0.0;
//    }
//
//    // Method to add a new participant to the team
//    public void addMember(Participant participant) {
//        members.add(participant);
//        calculateAverageSkill();
//    }
//
//    // Calculate the average skill of all team members
//    private void calculateAverageSkill() {
//        if (members.isEmpty()) {
//            averageSkill = 0;
//            return;
//        }
//
//        int totalSkill = 0;
//        for (Participant p : members) {
//            totalSkill += p.getSkillLevel();
//        }
//        averageSkill = (double) totalSkill / members.size();
//    }
//
//    // Getter methods
//    public int getTeamID() { return teamID; }
//    public List<Participant> getMembers() { return members; }
//    public double getAverageSkill() { return averageSkill; }
//
//    // Display team details
//    public void displayTeam() {
//        System.out.println("\nModel.Team ID: " + teamID);
//        System.out.println("Average Skill: " + averageSkill);
//        System.out.println("Members:");
//        for (Participant p : members) {
//            System.out.println(" - " + p.getName() + " (" + p.getPersonalityType() + ")");
//        }
//    }
//}
package Model;
import java.util.ArrayList;
import java.util.List;

public class Team {
    private int teamID;
    private List<Participant> members = new ArrayList<>();

    public Team(int id) { this.teamID = id; }
    public int getTeamID() { return teamID; }
    public List<Participant> getMembers() { return members; }
    public void addMember(Participant p) { members.add(p); }
    public double averageSkill() {
        if (members.isEmpty()) return 0.0;
        double s = 0;
        for (Participant p : members) s += p.getSkillLevel();
        return s / members.size();
    }
    public void displayTeam() {
        System.out.println("\nTeam-" + teamID + " | AvgSkill: " + String.format("%.2f", averageSkill()));
        for (Participant p : members) {
            System.out.println(" - " + p.getId() + " | " + p.getName() + " | " + p.getRole() + " | " + p.getGame() + " | " + p.getPersonalityType());
        }
    }
}

//import java.util.ArrayList;
//import java.util.List;
//
//public class Team {
//    private int teamID;
//    private List<Participant> members = new ArrayList<>();
//
//    public Team(int id) {
//        this.teamID = id;
//    }
//
//    public int getTeamID() { return teamID; }
//    public List<Participant> getMembers() { return members; }
//    public void addMember(Participant p) { members.add(p); }
//
//    public double averageSkill() {
//        if (members.isEmpty()) return 0.0;
//        double sum = 0;
//        for (Participant p : members) sum += p.getSkillLevel();
//        return sum / members.size();
//    }
//
//    public void displayTeam() {
//        System.out.println("\nTeam ID: " + teamID + " | AvgSkill: " + String.format("%.2f", averageSkill()));
//        for (Participant p : members) {
//            System.out.println(" - " + p.getId() + " | " + p.getName() + " | " + p.getRole() + " | " + p.getGame() + " | " + p.getPersonalityType());
//        }
//    }
//}
