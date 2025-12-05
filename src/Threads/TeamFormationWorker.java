package Threads;

import Logic.TeamBuilder;
import Model.Participant;
import Model.Team;

import java.util.List;

/**
 * TeamFormationWorker - runnable wrapper for TeamBuilder
 */
public class TeamFormationWorker extends TaskBase {
    private final int teamSize;
    private final int startId;
    private TeamBuilder builder;

    public TeamFormationWorker(List<Participant> participants, int teamSize, int startId1) {
        super(participants);
        this.teamSize = teamSize;
        this.startId = startId1;
    }

    @Override
    //sq number 1.5 in form teams use case of organizer
    public void run() {
        System.out.println("[TeamFormationWorker] Starting formation...");
        // Use 'participants' (inherited field) instead of 'candidates'
        //sq number 1.5.1 in form teams use case of organizer
        builder = new TeamBuilder(participants, teamSize, startId);
        //sq number 1.5.2 in form teams use case of organizer
        builder.formTeams();
        System.out.println("[TeamFormationWorker] Formation done.");
    }

    public List<Team> getTeams() { return (builder == null) ? List.of() : builder.getTeams(); }
    public List<Participant> getLeftovers() { return (builder == null) ? List.of() : builder.getLeftovers(); }
}
