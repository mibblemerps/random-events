package net.mitchfizz05.randomevents.eventsystem.component;

/**
 * Component for events that last over a period of time.
 */
public class CLongEvent implements IComponent
{
    private boolean isActive = false;

    /**
     * Is the event currently active?
     */
    public boolean isActive()
    {
        return isActive;
    }
}
