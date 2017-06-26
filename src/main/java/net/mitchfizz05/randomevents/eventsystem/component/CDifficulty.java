package net.mitchfizz05.randomevents.eventsystem.component;

import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;

/**
 * Event difficulty component
 */
public class CDifficulty implements IComponent
{
    public EventDifficulty difficulty;

    public CDifficulty(EventDifficulty difficulty)
    {
        this.difficulty = difficulty;
    }
}
