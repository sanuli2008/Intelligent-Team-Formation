package Model;

/**
 * Participant - data model for a participant
 *
 * Purpose:
 *  - Encapsulates participant fields that map to CSV columns.
 *
 * OOP concepts:
 *  - Encapsulation: private fields + public getters/setters.
 */
public class Participant {
    private String id;                // P### format
    private String name;
    private String email;
    private String game;
    private int skillLevel;           // 1-10
    private String role;              // Strategist, Attacker, Defender, Supporter, Coordinator
    private int personalityScore;     // 20..100
    private String personalityType;   // Leader, Balanced, Thinker
    private String assigned;          // "Unassigned" or "Team-#"

    public Participant(String id, String name, String email, String game,
                       int skillLevel, String role, int personalityScore, String personalityType, String assigned) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.game = game;
        this.skillLevel = skillLevel;
        this.role = role;
        this.personalityScore = personalityScore;
        this.personalityType = personalityType;
        this.assigned = (assigned == null || assigned.isEmpty()) ? "Unassigned" : assigned;
    }

    public Participant(String name, String email, String game,
                       int skillLevel, String role, int personalityScore, String personalityType) {
        this(null, name, email, game, skillLevel, role, personalityScore, personalityType, "Unassigned");
    }

    // Getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGame() { return game; }
    public int getSkillLevel() { return skillLevel; }
    public String getRole() { return role; }

    public int getPersonalityScore() { return personalityScore; }
    public void setPersonalityScore(int s) { this.personalityScore = s; }

    public String getPersonalityType() { return personalityType; }
    public void setPersonalityType(String t) { this.personalityType = t; }

    public String getAssigned() { return assigned; }
    public void setAssigned(String a) { this.assigned = (a == null || a.isEmpty()) ? "Unassigned" : a; }

    //for writing in csv
    @Override
    public String toString() {
        return String.join(",",
                safe(id), safe(name), safe(email), safe(game),
                String.valueOf(skillLevel), safe(role),
                String.valueOf(personalityScore), safe(personalityType), safe(assigned));
    }

    private String safe(String s) { return s == null ? "" : s.replaceAll(",", ""); }
}
