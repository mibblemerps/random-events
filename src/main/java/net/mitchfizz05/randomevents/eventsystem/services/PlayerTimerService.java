package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.util.WorldHelper;

import java.util.List;

/**
 * Handles {@link net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer} components.
 */
public class PlayerTimerService
{
    public PlayerTimerService()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        World world = event.player.getEntityWorld();

        if (!WorldHelper.isWorldLoaded() || !WorldHelper.isOverworld(world) || world.isRemote)
            return;

        // Only execute on 1 phase (otherwise this will execute twice per tick)
        if (event.phase != TickEvent.Phase.START) return;

        // Once per second
        if (event.player.ticksExisted % 20 != 0)
            return;

        // Get all applicable events
        List<RandomEvent> events = RandomEvents.randomEventRegistry.getWith(CPlayerTimer.class);

        // Tick each one
        for (RandomEvent randomEvent : events) {
            CPlayerTimer playerTimer = (CPlayerTimer) randomEvent.getComponent(CPlayerTimer.class);

            CWorldTimer timer = playerTimer.getTimer(event.player.getUniqueID());

            // Check if the timer has been initialised yet.
            if (timer.targetTime == 0)
                timer.reset();

            timer.timeElapsed ++;
            RandomEventServices.nbtService.markDirty();

            if (timer.timeElapsed >= timer.targetTime) {
                // Time to execute
                try {
                    RandomEventServices.executeEventService.executeEvent(randomEvent, world, event.player);
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
