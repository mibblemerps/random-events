package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.IUsesConfig;
import net.mitchfizz05.randomevents.eventsystem.component.IComponent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

/**
 * Handles config for Random Events
 */
public class ConfigService
{
    private Configuration config;

    public ConfigService(Configuration config)
    {
        this.config = config;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRandomEventsInitialised(RandomEvents.RandomEventsInitialised event)
    {
        // Loop through all events
        for (RandomEvent randomEvent : event.getRandomEventRegistry().randomEvents) {
            // Check if this event has been disabled in config
            if (!config.get(randomEvent.getConfigName(), "enabled", true,
                    "Is it possible for this event trigger?").getBoolean()) {
                // Event disabled
                randomEvent.disable();
                continue;
            }

            for (IComponent component : randomEvent.getComponents()) {
                if (component instanceof IUsesConfig) {
                    ((IUsesConfig) component).readConfig(config);
                }
            }
        }

        if (config.hasChanged())
            config.save();
    }
}
