package net.mitchfizz05.randomevents.eventsystem.component;

import net.minecraft.client.resources.I18n;

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

    /**
     * Get the localised announcement.
     */
    public String getLocalisedAnnouncement()
    {
        return I18n.format(translationKey);
    }
}
