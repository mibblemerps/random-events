package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.IEventTick;
import net.mitchfizz05.randomevents.eventsystem.component.CLongEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.util.WorldHelper;

import java.util.List;

/**
 * Runs long-running events (events with the {@link CLongTimedEvent}
 * component).
 */
public class LongEventService
{
    public LongEventService()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!WorldHelper.isWorldLoaded() || !WorldHelper.isOverworld(event.world) || event.world.isRemote)
            return;

        // Only execute on 1 phase (otherwise this will execute twice per tick)
        if (event.phase != TickEvent.Phase.START) return;

        // Tick everything
        tickLongEvents(event.world);
        tickLongEventTimers(event.world);
    }

    /**
     * Tick all long events to allow them to do their effects.
     */
    private void tickLongEvents(World world)
    {
        // Tick all long events that are active
        List<RandomEvent> events = RandomEvents.randomEventRegistry.getWith(CLongEvent.class);
        for (RandomEvent event : events) {
            // Check if this long event is active
            if (!((CLongEvent) event.getComponent(CLongEvent.class)).isActive())
                continue;

            // Check if the event has logic to do each tick, if it does - tick it.
            if (event instanceof IEventTick) {
                // Tick event
                ((IEventTick) event).tick(world, null);
            }
        }
    }

    /**
     * Decrement all active long event timers.
     */
    private void tickLongEventTimers(World world)
    {
        // Once per second, decrement all long timed events
        if (world.getTotalWorldTime() % 20 == 0) {
            // Get all applicable events
            List<RandomEvent> events = RandomEvents.randomEventRegistry.getWith(CLongTimedEvent.class);

            // Decrement all long timed events.
            for (RandomEvent event : events) {
                CLongTimedEvent cLongTimedEvent = (CLongTimedEvent) event.getComponent(CLongTimedEvent.class);

                // Decrement time left
                if (cLongTimedEvent.isActive()) {
                    cLongTimedEvent.timeLeft--;
                    RandomEventServices.nbtService.markDirty();
                }
            }
        }
    }

    /**
     * Is an event currently active?
     * If it's an event that doesn't last over a period of time, this will always return false.
     *
     * @param randomEvent Random event
     * @return Is active?
     */
    public static boolean isActive(RandomEvent randomEvent)
    {
        // If the event isn't a long event, it is never "active".
        if (!randomEvent.hasComponent(CLongEvent.class))
            return false;

        CLongEvent cLongEvent = (CLongEvent) randomEvent.getComponent(CLongEvent.class);
        return cLongEvent.isActive();
    }
}
