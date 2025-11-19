//package Logic;
//
//import Model.Participant;
//
//public class PersonalityClassifier {
//
//    // Threshold values
//    private int thresholdLeader;
//    private int thresholdBalanced;
//    private int thresholdThinker;
//
//    // Constructor - default thresholds
//    public PersonalityClassifier() {
//        this.thresholdLeader = 90;
//        this.thresholdBalanced = 70;
//        this.thresholdThinker = 50;
//    }
//
//    // Overloaded constructor - custom thresholds
//    public PersonalityClassifier(int leader, int balanced, int thinker) {
//        this.thresholdLeader = leader;
//        this.thresholdBalanced = balanced;
//        this.thresholdThinker = thinker;
//    }
//
//    // Classify participant based on their personality score
//    public String classify(Participant p) {
//        int score = p.getPersonalityScore();
//
//        if (score >= thresholdLeader) {
//            return "Leader";
//        } else if (score >= thresholdBalanced) {
//            return "Balanced";
//        } else if (score >= thresholdThinker) {
//            return "Thinker";
//        } else {
//            return "Undefined";
//        }
//    }
//
//    // Optional: getter methods if you want to view thresholds
//    public int getThresholdLeader() { return thresholdLeader; }
//    public int getThresholdBalanced() { return thresholdBalanced; }
//    public int getThresholdThinker() { return thresholdThinker; }
//}




//import Model.Participant;
//
//public class PersonalityClassifier {
//
//    private int thresholdLeader = 90;
//    private int thresholdBalanced = 70;
//    private int thresholdThinker = 50;
//
//    public PersonalityClassifier() {}
//
//    public PersonalityClassifier(int leader, int balanced, int thinker) {
//        this.thresholdLeader = leader;
//        this.thresholdBalanced = balanced;
//        this.thresholdThinker = thinker;
//    }
//
//    // Classify by Participant object
//    public String classify(Participant p) {
//        return classify(p.getPersonalityScore());
//    }
//
//    // 🔹 Overloaded method — classify by numeric score directly
//    public String classify(int score) {
//        if (score >= thresholdLeader) return "Leader";
//        else if (score >= thresholdBalanced) return "Balanced";
//        else if (score >= thresholdThinker) return "Thinker";
//        else return "Undefined";
//    }
//}
package Logic;
public class PersonalityClassifier {
    private int thresholdLeader = 90;
    private int thresholdBalanced = 70;
    private int thresholdThinker = 50;

    public PersonalityClassifier() {}

    // classify by numeric score
    public String classify(int score) {
        if (score >= thresholdLeader) return "Leader";
        if (score >= thresholdBalanced) return "Balanced";
        if (score >= thresholdThinker) return "Thinker";
        return "Undefined";
    }

    // compute scaled personality based on five answers 1-5 (sum 5-25 -> scale*4)
    public int computeScoreFromAnswers(int q1,int q2,int q3,int q4,int q5) {
        int sum = q1 + q2 + q3 + q4 + q5; // 5..25
        return sum * 4; // scale to 20..100
    }
}

//import Model.Participant;
//
//public class PersonalityClassifier {
//
//    private int thresholdLeader = 90;
//    private int thresholdBalanced = 70;
//    private int thresholdThinker = 50;
//
//    public PersonalityClassifier() {}
//
//    // classify by numeric score
//    public String classify(int score) {
//        if (score >= thresholdLeader) return "Leader";
//        if (score >= thresholdBalanced) return "Balanced";
//        if (score >= thresholdThinker) return "Thinker";
//        return "Undefined";
//    }
//
//    // convenience: classify participant
//    public String classify(Participant p) {
//        return classify(p.getPersonalityScore());
//    }
//
//    // compute scaled personality based on five answers 1-5 (sum 5-25 -> scale*4)
//    public int computeScoreFromAnswers(int q1,int q2,int q3,int q4,int q5) {
//        int sum = q1 + q2 + q3 + q4 + q5; // 5..25
//        return sum * 4; // scale to 20..100
//    }
//}
