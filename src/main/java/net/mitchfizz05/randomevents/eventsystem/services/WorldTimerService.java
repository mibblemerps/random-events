package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.util.WorldHelper;

import java.util.List;

/**
 * Handles {@link net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer} components.
 */
public class WorldTimerService
{
    private double multiplier = 1.0;

    public WorldTimerService()
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

        // Once per second
        if (event.world.getTotalWorldTime() % 20 != 0)
            return;

        // Update multiplier
        multiplier = RandomEventServices.eventTimerMultiplierService.getMultiplier(event.world);

        // Get all applicable events
        List<RandomEvent> events = RandomEvents.randomEventRegistry.getWith(CWorldTimer.class);

        // Tick each one
        for (RandomEvent randomEvent : events) {
            CWorldTimer timer = (CWorldTimer) randomEvent.getComponent(CWorldTimer.class);

            // Check if the timer has been initialised yet.
            if (timer.targetTime == 0)
                timer.reset();

            timer.timeElapsed = timer.timeElapsed + multiplier;
            RandomEventServices.nbtService.markDirty();

            if (timer.timeElapsed >= timer.targetTime) {
                // Time to execute
                try {
                    RandomEventServices.executeEventService.executeEvent(randomEvent, event.world, null, true);
                } catch (ExecuteEventException e) {
                    e.printWarning();
                }

                // Reset timer
                timer.reset();
                RandomEventServices.nbtService.markDirty();
            }
        }
    }
}
