package Threads;

import Logic.TeamBuilder;
import Model.Participant;
import Model.Team;

import java.util.List;

/**
 * TeamFormationWorker - runnable wrapper for TeamBuilder
 */
public class TeamFormationWorker implements Runnable {
    private final List<Participant> candidates;
    private final int teamSize;
    private final int startId;
    private TeamBuilder builder;

    public TeamFormationWorker(List<Participant> candidates, int teamSize, int startId1) {
        this.candidates = candidates;
        this.teamSize = teamSize;
        this.startId = startId1;
    }

    @Override
    public void run() {
        System.out.println("[TeamFormationWorker] Starting formation...");
        builder = new TeamBuilder(candidates, teamSize, startId);
        builder.formTeams();
        System.out.println("[TeamFormationWorker] Formation done.");
    }

    public List<Team> getTeams() { return (builder == null) ? List.of() : builder.getTeams(); }
    public List<Participant> getLeftovers() { return (builder == null) ? List.of() : builder.getLeftovers(); }
}
