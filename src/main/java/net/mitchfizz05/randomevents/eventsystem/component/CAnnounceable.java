package net.mitchfizz05.randomevents.eventsystem.component;

/**
 * An event's announcement.
 * If this component isn't present, the event won't be announced when it triggers.
 */
public class CAnnounceable implements IComponent
{
    public String translationKey;

    public CAnnounceable(String translationKey)
    {
        this.translationKey = translationKey;
    }
}
