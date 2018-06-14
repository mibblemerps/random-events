package net.mitchfizz05.randomevents.eventsystem.services;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.eventsystem.component.CRandomPlayer;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.eventsystem.event.RandomEventTriggerEvent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Assists with executing events.
 */
public class ExecuteEventService
{
    protected double rescheduleMinimumPercentage = 0.5;

    public ExecuteEventService()
    {
        rescheduleMinimumPercentage = RandomEvents.config.get("general", "reschedule_minimum_percentage", rescheduleMinimumPercentage,
                "When an event fails to execute because conditions aren't right, the event is rescheduled to execute earlier than it would otherwise.\n" +
                        "This is the minimum percentage the target time will be reduced. Eg. 0.5 means the target time will be AT LEAST half as long.",
                0, 1).getDouble();
    }

    public void executeEvent(@Nonnull RandomEvent randomEvent, @Nonnull World world, @Nullable EntityPlayer player, boolean shouldRescheduleOnFail) throws ExecuteEventException
    {
        if (!randomEvent.isEnabled()) {
            throw new ExecuteEventException("Event disabled", randomEvent);
        }

        if (randomEvent.hasComponent(CRandomPlayer.class)) {
            player = getRandomPlayer(randomEvent, world.getMinecraftServer());
        }

        // Check if we need a target player and if we have one.
        if (randomEvent.hasComponent(CPlayerEvent.class) && (player == null)) {
            // Event needs player, but no player provided.
            throw new IllegalArgumentException(randomEvent.getName() + " requires a player, but none was provided.");
        }

        try {
            // Execute event
            randomEvent.execute(world, player);
        } catch (ExecuteEventException e) {
            // Event failed to execute
            if (shouldRescheduleOnFail) {
                // Reschedule event to happen sooner if it is a timer based event
                if (randomEvent.hasComponent(CWorldTimer.class)) {
                    rescheduleEvent((CWorldTimer) randomEvent.getComponent(CWorldTimer.class));
                }
                if (randomEvent.hasComponent(CPlayerTimer.class)) {
                    rescheduleEvent((CPlayerTimer) randomEvent.getComponent(CPlayerTimer.class));
                }
            }

            // Now throw the original exception to the caller
            throw e;
        }

        // Post to event bus
        MinecraftForge.EVENT_BUS.post(new RandomEventTriggerEvent(randomEvent, world, player));
    }

    public void executeEvent(@Nonnull RandomEvent randomEvent, @Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        executeEvent(randomEvent, world, player, false);
    }

    public void rescheduleEvent(CWorldTimer worldTimer)
    {
        worldTimer.reset();

        // Get a random reduction between the minimum reschedule percentage and 0.99
        double reduction = ThreadLocalRandom.current().nextDouble(rescheduleMinimumPercentage, 0.99);

        worldTimer.targetTime = worldTimer.targetTime - (int) Math.round(worldTimer.targetTime * reduction);
    }

    public void rescheduleEvent(CPlayerTimer playerTimer)
    {
        for (CWorldTimer timer : playerTimer.getPlayerTimers().values()){
            rescheduleEvent(timer);
        }
    }
    private EntityPlayer getRandomPlayer(RandomEvent randomEvent, MinecraftServer server) throws ExecuteEventException
    {
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();

        if (players.size() == 0)
            throw new ExecuteEventException("No players to execute event on", randomEvent);

        return players.get(ThreadLocalRandom.current().nextInt(0, players.size()));
    }
}
