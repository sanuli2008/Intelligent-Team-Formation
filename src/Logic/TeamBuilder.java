package Logic;

import Model.Participant;
import Model.Team;

import java.util.*;
import java.util.logging.Logger;

public class TeamBuilder {
    private static final Logger LOGGER = Logger.getLogger(TeamBuilder.class.getName());
    private final List<Participant> originalPool;
    private final int teamSize;
    private final int startId;
    private final Random rnd = new Random();
    private final ConstraintChecker checker = new ConstraintChecker();
    private final TeamBalancer balancer = new TeamBalancer(checker);

    private final List<Team> teams = new ArrayList<>();
    private final List<Participant> leftovers = new ArrayList<>();

    // tuning
    //I used 1500 attempts, since there are around 100 participants the permutation for that is vast.
    //this attempts ensure that skill balance is fairly distributed to the approximate of 20 teams.
    private final int BALANCE_ATTEMPTS = 1500;

    public TeamBuilder(List<Participant> pool, int teamSize, int startId) {
        this.originalPool = new ArrayList<>(pool);
        this.teamSize = teamSize;
        this.startId = startId;
    }

    public void formTeams() {
        LOGGER.info("Algorithm started. Pool size: " + originalPool.size());
        //clean old data
        teams.clear();
        leftovers.clear();

        // 1. Filter candidates (Unassigned only)
        List<Participant> candidates = new ArrayList<>();

        for (Participant p : originalPool) {
            String assigned = p.getAssigned();
            if (assigned == null || !assigned.toLowerCase().startsWith("team-")) {
                candidates.add(p);
            }
        }

        // 2. Separate into Pools
        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>(); // Balanced + Others

        //adding participants to lists according to personality type
        for (Participant p : candidates) {
            String t = p.getPersonalityType();
            if ("Leader".equalsIgnoreCase(t)) leaders.add(p);
            else if ("Thinker".equalsIgnoreCase(t)) thinkers.add(p);
            else balanced.add(p);
        }

        // 3. Determine Max Teams
        // constraint: we need exactly 1 leader per team.
        // no team can exist without a leader.
        int maxTeamsByLeaders = leaders.size();
        int maxTeamsByHeadcount = candidates.size() / teamSize;
        int numTeams = Math.min(maxTeamsByLeaders, maxTeamsByHeadcount);

        if (numTeams <= 0) {
            leftovers.addAll(candidates);
            markLeftovers();
            return;
        }

        // 4. Initialize Teams
//        for (int i = 1; i <= numTeams; i++) teams.add(new Team(i));
        for (int i = 0; i < numTeams; i++) {
            teams.add(new Team(startId + i)); // <--- USE startId
        }

        // Shuffle for fairness before assigning
        Collections.shuffle(leaders, rnd);
        Collections.shuffle(thinkers, rnd);
        Collections.shuffle(balanced, rnd);

        // PHASE 1: Mandatory Leader (Exactly 1)
        for (Team t : teams) {
            t.addMember(leaders.remove(0));
        }
        // Any remaining leaders are now leftovers. They cannot be added to teams
        // because of the strict 1 Leader rule.
        leftovers.addAll(leaders);
        leaders.clear(); // Clear to ensure no duplicates later

        // PHASE 2: Mandatory Thinker (At least 1)
        for (Team t : teams) {
            if (thinkers.isEmpty()) break;
            Participant p = thinkers.remove(0);
            //checking using canAddToTeam, to make sure teams follow rules
            if (checker.canAddToTeam(t, p, teamSize)) {
                t.addMember(p);
            } else {
                // If specific thinker conflicts (e.g. game cap), try others or move to leftovers
                // For simplicity in this phase,  push back to list or swap logic,
                // but here  just put back to pool for Phase 3 if rejected.
                balanced.add(p); // Treat as general pool for now
            }
        }

        // PHASE 3: Fill Remaining Slots (Mixed)
        // Pool consists of remaining Thinkers + All Balanced.
        // We sort by skill (Descending) to ensure the best players get picked first.
        //used greedy approach
        List<Participant> pool = new ArrayList<>();
        pool.addAll(thinkers);
        pool.addAll(balanced);
        // Sort by SKILL (High to Low)
        pool.sort((a, b) -> Integer.compare(b.getSkillLevel(), a.getSkillLevel()));

        for (Participant cand : new ArrayList<>(pool)) {
            boolean placed = false;

            // Try to find a team that fits
            // Priority: Teams that are not full
            for (Team t : teams) {
                if (t.size() >= teamSize) continue;

                // Checker enforces: <2 Thinkers, Game Cap, etc.
                if (checker.canAddToTeam(t, cand, teamSize)) {
                    t.addMember(cand);
                    pool.remove(cand);
                    placed = true;
                    break;
                }
            }
        }

        // Anything remaining in 'pool' is leftovers
        leftovers.addAll(pool);

        // Skill Balancing (swapping members between teams to even out averages)
        balancer.balance(teams, BALANCE_ATTEMPTS, teamSize);

        // PHASE 4: Final Validation
        //This handles team size and safety
        Iterator<Team> it = teams.iterator();
        while (it.hasNext()) {
            Team t = it.next();
            // Final strict check: 1 Leader, 1-2 Thinkers, Size == N
            if (!checker.validateFinalTeam(t, teamSize)) {
                leftovers.addAll(t.getMembers());
                it.remove();
            }
        }

        // Re-index teams (Team-1, Team-2...)
        reindexNonEmptyTeams();

        // Assign strings for output
        for (Team t : teams) for (Participant p : t.getMembers()) p.setAssigned("Team-" + t.getTeamID());
        markLeftovers();
    }

    //Simply keeps teams that have members and gives them IDs 1, 2, 3...
//    private void reindexNonEmptyTeams() {
//        List<Team> nonEmpty = new ArrayList<>();
//        for (Team t : teams) {
//            if (t.size() > 0) {
//                nonEmpty.add(t);
//            }
//        }
//        teams.clear();
//        for (int i = 0; i < nonEmpty.size(); i++) {
//            Team old = nonEmpty.get(i);
//            Team nt = new Team(i + 1); // New ID
//            for (Participant p : old.getMembers()) {
//                nt.addMember(p);
//            }
//            teams.add(nt);
//        }
//    }
    private void reindexNonEmptyTeams() {
        List<Team> nonEmpty = new ArrayList<>();
        for (Team t : teams) {
            if (t.size() > 0){
                nonEmpty.add(t);
            };
        }
        teams.clear();
        for (int i = 0; i < nonEmpty.size(); i++) {
            Team old = nonEmpty.get(i);
            Team nt = new Team(startId + i);
            for (Participant p : old.getMembers()){
                nt.addMember(p);
            }
            teams.add(nt);
        }
    }


    private void markLeftovers() {
        for (Participant p : leftovers) p.setAssigned("Unassigned");
    }

    public List<Team> getTeams() { return new ArrayList<>(teams); }
    public List<Participant> getLeftovers() { return new ArrayList<>(leftovers); }
}