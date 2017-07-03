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
        // Loop through all events and their components and load their config if needed.
        for (RandomEvent randomEvent : event.getRandomEventRegistry().randomEvents) {
            for (IComponent component : randomEvent.getComponents()) {
                if (component instanceof IUsesConfig) {
                    ((IUsesConfig) component).readConfig(config);
                    RandomEvents.logger.info("Loaded config for " + randomEvent.toString() + "/" + component.toString());
                }
            }
        }

        if (config.hasChanged())
            config.save();
    }
}
