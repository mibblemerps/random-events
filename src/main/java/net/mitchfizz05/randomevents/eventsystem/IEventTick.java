package net.mitchfizz05.randomevents.eventsystem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * For events that need to be ticked.
 * At the moment exclusively for events that have the
 * {@link CLongTimedEvent} component.
 */
public interface IEventTick
{
    /**
     * Where the event should do it's per-tick logic.
     *
     * @param world World object
     * @param player Target player
     */
    void tick(@Nonnull World world, @Nullable EntityPlayer player);
}
