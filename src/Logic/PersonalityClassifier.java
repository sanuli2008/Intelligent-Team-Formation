package Logic;

import Model.Participant;

// to get the score from 5 question survey and classifies into types.

public class PersonalityClassifier {

    // Compute scaled score: sum(5..25) * 4 -> 20..100
    public int computeScore(int q1, int q2, int q3, int q4, int q5) {
        int sum = q1 + q2 + q3 + q4 + q5;
        return sum * 4;
    }

    public String classify(int score) {
        if (score >= 90) return "Leader";
        if (score >= 70) return "Balanced";
        if (score >= 50) return "Thinker";
        return "Undefined";
    }

    public void ensureType(Participant p) {
        if ((p.getPersonalityType() == null || p.getPersonalityType().isEmpty()) && p.getPersonalityScore() > 0) {
            p.setPersonalityType(classify(p.getPersonalityScore()));
        }
    }
}
