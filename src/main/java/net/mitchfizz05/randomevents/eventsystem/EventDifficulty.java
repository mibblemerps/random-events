package net.mitchfizz05.randomevents.eventsystem;

import net.minecraft.util.text.TextFormatting;

/**
 * Difficulty of events.
 */
public enum EventDifficulty
{
    /**
     * Horrendously bad. Be scared if one of these happen.
     */
    VERY_BAD,
    /**
     * An overall bad but manageable event.
     */
    BAD,
    /**
     * A neutral event. Maybe it's partially good and bad, or maybe it could be either and the player doesn't know yet.
     */
    NEUTRAL,
    /**
     * An event that benefits the player to at least some slight degree. Even something small like a trader coming.
     */
    GOOD,
    /**
     * An event that greatly benefits the player, example might be a cargo drop or something.
     */
    VERY_GOOD,

    /**
     * god forbid
     */
    HELL;

    /**
     * Is this event a generally positive difficulty?
     */
    public boolean isPositive()
    {
        switch (this) {
            case GOOD:
            case VERY_GOOD:
                return true;
        }
        return false;
    }

    /**
     * Is this event a generally negative difficulty?
     */
    public boolean isNegative()
    {
        switch (this) {
            case VERY_BAD:
            case BAD:
            case HELL:
                return true;
        }
        return false;
    }

    /**
     * Is this event a neutral difficulty?
     */
    public boolean isNeutral()
    {
        return this == NEUTRAL;
    }

    /**
     * Get an appropriate text colour for this difficulty.
     */
    public TextFormatting getColor()
    {
        if (isPositive())
            return TextFormatting.DARK_GREEN;
        else if (isNegative())
            return TextFormatting.RED;
        else if (isNeutral())
            return TextFormatting.AQUA;
        else if (this == HELL)
            return TextFormatting.BLACK;

        return TextFormatting.AQUA; // default
    }
}
