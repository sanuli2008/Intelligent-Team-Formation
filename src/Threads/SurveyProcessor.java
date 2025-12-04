package Threads;

import Logic.PersonalityClassifier;
import Model.Participant;

import java.util.List;

/**
 * SurveyProcessor - ensures personality types are available (Runnable)
 */
public class SurveyProcessor implements Runnable {
    private final List<Participant> participants;
    private final PersonalityClassifier classifier;

    public SurveyProcessor(List<Participant> participants, PersonalityClassifier classifier) {
        this.participants = participants;
        this.classifier = classifier;
    }

    @Override
    public void run() {
        System.out.println("[SurveyProcessor] Processing survey data...");
        for (Participant p : participants) {
            classifier.ensureType(p);
            try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        }
        System.out.println("[SurveyProcessor] Done.");
    }
}
