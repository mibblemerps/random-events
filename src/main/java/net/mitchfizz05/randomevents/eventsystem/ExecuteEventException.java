package net.mitchfizz05.randomevents.eventsystem;

import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

/**
 * An randomevent was unable to execute for whatever reason.
 */
public class ExecuteEventException extends Exception
{
    public RandomEvent event;
    public boolean allowRescheduling;

    /**
     * @param message Why the event couldn't execute
     * @param event The event that couldn't execute
     * @param allowRescheduling If this event failed to execute on timer, should it be rescheduled to be executed earlier? Not applicable to events that don't run on timer.
     */
    public ExecuteEventException(String message, RandomEvent event, boolean allowRescheduling)
    {
        super(message);
        this.event = event;
        this.allowRescheduling = allowRescheduling;
    }

    /**
     * This constructor sets allowRescheduling to true. This is suitable for most cases or if the event doesn't run on a timer.
     *
     * @param message Why the event couldn't execute
     * @param event The event that couldn't execute
     */
    public ExecuteEventException(String message, RandomEvent event)
    {
        this(message, event, true);
    }

    public void printWarning()
    {
        RandomEvents.logger.warn(String.format("Failed to execute event \"%s\"! %s", event.getName(), getMessage()));
    }
}
