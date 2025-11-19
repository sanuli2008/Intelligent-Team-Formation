//package Model;
//
//public class Participant {
//    // Attributes
//    private String name;
//    private String game;
//    private String role;
//    private int skillLevel;
//    private int personalityScore;    // keep raw score if CSV contains it or we compute it
//    private String personalityType;  // "Leader", "Balanced", "Thinker", etc.
//
//    // Constructors
//    public Participant(String name, String game, String role, int skillLevel, int personalityScore) {
//        this.name = name;
//        this.game = game;
//        this.role = role;
//        this.skillLevel = skillLevel;
//        this.personalityScore = personalityScore;
//        this.personalityType = null; // will be assigned by Logic.PersonalityClassifier
//    }
//
//    // Optional convenience constructor if you want to set personalityType directly
//    public Participant(String name, String game, String role, int skillLevel, int personalityScore, String personalityType) {
//        this(name, game, role, skillLevel, personalityScore);
//        this.personalityType = personalityType;
//    }
//
//    // Getters and setters
//    public String getName() { return name; }
//    public String getGame() { return game; }
//    public String getRole() { return role; }
//    public int getSkillLevel() { return skillLevel; }
//    public int getPersonalityScore() { return personalityScore; }
//    public String getPersonalityType() { return personalityType; }
//
//    public void setPersonalityScore(int score) { this.personalityScore = score; }
//    public void setPersonalityType(String type) { this.personalityType = type; }
//
//    // For debugging
//    public void displayInfo() {
//        System.out.println("Model.Participant: " + name +
//                " | Game: " + game +
//                " | Role: " + role +
//                " | Skill: " + skillLevel +
//                " | PersonalityScore: " + personalityScore +
//                " | PersonalityType: " + personalityType);
//    }
//}



//public class Participant {
//
//    private String id;
//    private String name;
//    private String email;
//    private String game;
//    private int skillLevel;
//    private String role;
//    private int personalityScore;
//    private String personalityType;
//
//    public Participant(String id, String name, String email, String game, int skillLevel,
//                       String role, int personalityScore, String personalityType) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.game = game;
//        this.skillLevel = skillLevel;
//        this.role = role;
//        this.personalityScore = personalityScore;
//        this.personalityType = personalityType;
//    }
//
//    // constructor without ID (for new entries)
//    public Participant(String name, String email, String game, int skillLevel,
//                       String role, int personalityScore, String personalityType) {
//        this(null, name, email, game, skillLevel, role, personalityScore, personalityType);
//    }
//
//    // getters & setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getName() { return name; }
//    public String getEmail() { return email; }
//    public String getGame() { return game; }
//    public int getSkillLevel() { return skillLevel; }
//    public String getRole() { return role; }
//    public int getPersonalityScore() { return personalityScore; }
//    public String getPersonalityType() { return personalityType; }
//    public void setPersonalityType(String type) { this.personalityType = type; }
//
//    @Override
//    public String toString() {
//        return id + " - " + name + " (" + personalityType + ")";
//    }
//}
package Model;
public class Participant {
    private String id;
    private String name;
    private String email;
    private String game;
    private int skillLevel;           // 1-10
    private String role;              // Strategist, Attacker, Defender, Supporter, Coordinator
    private int personalityScore;     // scaled 0-100
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
        this.assigned = assigned == null || assigned.isEmpty() ? "Unassigned" : assigned;
    }

    public Participant(String name, String email, String game,
                       int skillLevel, String role, int personalityScore, String personalityType) {
        this(null, name, email, game, skillLevel, role, personalityScore, personalityType, "Unassigned");
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGame() { return game; }
    public int getSkillLevel() { return skillLevel; }
    public String getRole() { return role; }
    public int getPersonalityScore() { return personalityScore; }
    public String getPersonalityType() { return personalityType; }
    public String getAssigned() { return assigned; }

    public void setPersonalityScore(int s) { this.personalityScore = s; }
    public void setPersonalityType(String t) { this.personalityType = t; }
    public void setAssigned(String a) { this.assigned = a; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%d,%s,%d,%s,%s",
                safe(id), safe(name), safe(email), safe(game), skillLevel, safe(role), personalityScore, safe(personalityType), safe(assigned));
    }

    private String safe(String s) { return s == null ? "" : s; }
}

//public class Participant {
//    private String id;
//    private String name;
//    private String email;
//    private String game;
//    private int skillLevel;           // 1-10
//    private String role;              // Strategist, Attacker, Defender, Supporter, Coordinator
//    private int personalityScore;     // scaled 0-100
//    private String personalityType;   // Leader, Balanced, Thinker
//
//    public Participant(String id, String name, String email, String game,
//                       int skillLevel, String role, int personalityScore, String personalityType) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.game = game;
//        this.skillLevel = skillLevel;
//        this.role = role;
//        this.personalityScore = personalityScore;
//        this.personalityType = personalityType;
//    }
//
//    // constructor when ID unknown (will be set by FileManager.appendParticipant)
//    public Participant(String name, String email, String game,
//                       int skillLevel, String role, int personalityScore, String personalityType) {
//        this(null, name, email, game, skillLevel, role, personalityScore, personalityType);
//    }
//
//    // getters / setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getName() { return name; }
//    public String getEmail() { return email; }
//    public String getGame() { return game; }
//    public int getSkillLevel() { return skillLevel; }
//    public String getRole() { return role; }
//    public int getPersonalityScore() { return personalityScore; }
//    public String getPersonalityType() { return personalityType; }
//    public void setPersonalityScore(int s) { this.personalityScore = s; }
//    public void setPersonalityType(String t) { this.personalityType = t; }
//
//    @Override
//    public String toString() {
//        return String.format("%s - %s (%s) [%s] Skill:%d Score:%d Type:%s",
//                id == null ? "NOID" : id, name, email, game, skillLevel, personalityScore, personalityType);
//    }
//}
