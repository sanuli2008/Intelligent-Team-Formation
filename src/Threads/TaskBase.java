package Threads;

import Model.Participant;
import java.util.List;

// ABSTRACT CLASS: Satisfies "Abstraction" and allows "Inheritance"
public abstract class TaskBase implements Runnable {

    // PROTECTED: Children can access this directly
    protected List<Participant> participants;

    public TaskBase(List<Participant> participants) {
        this.participants = participants;
    }

    // We leave run() abstract (from Runnable) so children must implement it
}