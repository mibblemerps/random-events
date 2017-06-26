package net.mitchfizz05.randomevents.eventsystem;

import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

/**
 * An randomevent was unable to execute for whatever reason.
 */
public class ExecuteEventException extends Exception
{
    public RandomEvent event;

    public ExecuteEventException(String message, RandomEvent event)
    {
        super(message);
        this.event = event;
    }

    public void printWarning()
    {
        RandomEvents.logger.warn(String.format("Failed to execute event \"%s\"! %s", event.getName(), getMessage()));
    }
}
