package Logic;

import Model.Participant;
import Model.Team;

import java.util.List;
import java.util.Random;

//handles soft constraints
//tries to make teams fair and balanced
public class TeamBalancer {

    private final Random rnd = new Random();
    private final ConstraintChecker checker;

    public TeamBalancer(ConstraintChecker checker) {
        this.checker = checker;
    }

    //use stochastic(random) hill climbing
    //pick 2 random teams, then pick 2 random players,if variance goes down,then keep swap, if not undo swap
    public void balance(List<Team> teams, int attempts, int teamSize) {
        if (teams == null || teams.size() <= 1) return;

        double bestVar = variance(teams);

        for (int i = 0; i < attempts; i++) {
            int a = rnd.nextInt(teams.size()), b = rnd.nextInt(teams.size());
            if (a == b) continue;

            Team t1 = teams.get(a), t2 = teams.get(b);
            if (t1.getMembers().isEmpty() || t2.getMembers().isEmpty()) continue;

            Participant p1 = t1.getMembers().get(rnd.nextInt(t1.getMembers().size()));
            Participant p2 = t2.getMembers().get(rnd.nextInt(t2.getMembers().size()));

            long t1LeadersBefore = checker.leaderCount(t1);
            long t2LeadersBefore = checker.leaderCount(t2);
            long t1ThinkersBefore = checker.thinkerCount(t1);
            long t2ThinkersBefore = checker.thinkerCount(t2);

            if ("Leader".equalsIgnoreCase(p1.getPersonalityType()) && t1LeadersBefore <= 1) continue;
            if ("Leader".equalsIgnoreCase(p2.getPersonalityType()) && t2LeadersBefore <= 1) continue;
            if ("Thinker".equalsIgnoreCase(p1.getPersonalityType()) && t1ThinkersBefore <= 1) continue;
            if ("Thinker".equalsIgnoreCase(p2.getPersonalityType()) && t2ThinkersBefore <= 1) continue;

            // Tentative swap
            t1.getMembers().remove(p1); t2.getMembers().remove(p2);

            boolean ok1 = checker.canAddToTeam(t1, p2, teamSize);
            boolean ok2 = checker.canAddToTeam(t2, p1, teamSize);

            if (ok1 && ok2) {
                t1.getMembers().add(p2); t2.getMembers().add(p1);
                double nv = variance(teams);
                if (nv < bestVar) {
                    bestVar = nv; // accept swap
                } else {
                    // rollback
                    t1.getMembers().remove(p2); t2.getMembers().remove(p1);
                    t1.getMembers().add(p1); t2.getMembers().add(p2);
                }
            } else {
                // rollback
                t1.getMembers().add(p1); t2.getMembers().add(p2);
            }
        }
    }

    //a low variance means team average is closer to each other
    private double variance(List<Team> teams) {
        if (teams.isEmpty()) return 0.0;

        double sum = 0;
        for (Team t : teams) {
            sum += t.averageSkill();
        }
        double mean = sum / teams.size();

        double sqDiffSum = 0;
        for (Team t : teams) {
            double diff = t.averageSkill() - mean;
            sqDiffSum += (diff * diff);
        }

        return sqDiffSum / teams.size();
    }
}