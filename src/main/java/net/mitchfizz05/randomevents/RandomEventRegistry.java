package net.mitchfizz05.randomevents;

import net.mitchfizz05.randomevents.eventsystem.component.IComponent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEventAcidRain;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEventBlight;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEventForestFire;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RandomEventRegistry
{
    public ArrayList<RandomEvent> randomEvents;

    public RandomEventRegistry()
    {
        randomEvents = new ArrayList<RandomEvent>();
    }

    /**
     * Register a new random randomevent.
     *
     * @param event New randomevent
     */
    public void register(RandomEvent event)
    {
        randomEvents.add(event);
    }

    /**
     * Find a random randomevent by name.
     *
     * @param name Event name
     * @return Random randomevent. Null if not found.
     */
    public RandomEvent get(String name)
    {
        for (RandomEvent event : randomEvents) {
            if (event.getName().equals(name))
                return event;
        }

        return null;
    }

    /**
     * Get all events with a particular component.
     *
     * @param component Component to search for
     * @return List of events
     */
    public List<RandomEvent> getWith(Class<? extends IComponent> component)
    {
        List<RandomEvent> eventsFound = new ArrayList<RandomEvent>();

        for (RandomEvent event : randomEvents) {
            if (event.hasComponent(component)) {
                eventsFound.add(event);
            }
        }

        return eventsFound;
    }


    // ---

    /**
     * Register all the events in the mod.
     */
    public void registerAllEvents()
    {
        register(new RandomEventBlight());
        register(new RandomEventAcidRain());
        register(new RandomEventForestFire());
    }
}
