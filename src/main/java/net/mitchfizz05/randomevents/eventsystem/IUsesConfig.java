package net.mitchfizz05.randomevents.eventsystem;

import net.minecraftforge.common.config.Configuration;

/**
 * Indicates this object uses the mod config.
 */
public interface IUsesConfig
{
    /**
     * When you should read your config values.
     *
     * @param config Config object to read from.
     */
    void readConfig(Configuration config);
}
