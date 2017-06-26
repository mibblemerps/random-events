package net.mitchfizz05.randomevents.eventsystem.services;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CRandomPlayer;
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
    public void executeEvent(@Nonnull RandomEvent randomEvent, @Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        if (randomEvent.hasComponent(CRandomPlayer.class)) {
            player = getRandomPlayer(randomEvent, world.getMinecraftServer());
        }

        // Check if we need a target player and if we have one.
        if (randomEvent.hasComponent(CPlayerEvent.class) && (player == null)) {
            // Event needs player, but no player provided.
            throw new IllegalArgumentException(randomEvent.getName() + " requires a player, but none was provided.");
        }

        // Execute event
        randomEvent.execute(world, player);

        // Post to event bus
        MinecraftForge.EVENT_BUS.post(new RandomEventTriggerEvent(randomEvent, world, player));
    }


    private EntityPlayer getRandomPlayer(RandomEvent randomEvent, MinecraftServer server) throws ExecuteEventException
    {
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();

        if (players.size() == 0)
            throw new ExecuteEventException("No players to execute event on", randomEvent);

        return players.get(ThreadLocalRandom.current().nextInt(0, players.size()));
    }
}
