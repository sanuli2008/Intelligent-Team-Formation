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
//sq number 1.4 in form teams use case of organizer
    public void run() {
        System.out.println("[SurveyProcessor] Processing survey data...");
        //can access 'participants' directly because it is in the parent
        for (Participant p : participants) {
            //sq number 1.4.1 in form teams use case of organizer
            classifier.ensureType(p);
            try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        }
        System.out.println("[SurveyProcessor] Done.");
    }
}
