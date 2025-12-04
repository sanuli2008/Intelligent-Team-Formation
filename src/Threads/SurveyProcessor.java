package Threads;

import Logic.PersonalityClassifier;
import Model.Participant;

import java.util.List;

/**
 * SurveyProcessor - ensures personality types are available (Runnable)
 */
public class SurveyProcessor extends TaskBase {
    private final PersonalityClassifier classifier;

    public SurveyProcessor(List<Participant> participants, PersonalityClassifier classifier) {
        super(participants);
        this.classifier = classifier;
    }

    @Override
    public void run() {
        System.out.println("[SurveyProcessor] Processing survey data...");
        //can access 'participants' directly because it is in the parent
        for (Participant p : participants) {
            classifier.ensureType(p);
            try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        }
        System.out.println("[SurveyProcessor] Done.");
    }
}
