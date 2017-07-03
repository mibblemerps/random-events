package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CDifficulty;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.MobSpawner;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A wolf tamed to the player spawns nearby.
 */
public class RandomEventTameWolf extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    public RandomEventTameWolf()
    {
        super("tame_wolf");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.GOOD;

        addComponent(new CPlayerTimer(this, TimeHelper.minsToTicks(15), TimeHelper.hrsToTicks(3)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.execute(this, new MobSpawner.MobSpawnEventParameters(1, 1, 8), world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntityWolf wolf = new EntityWolf(world);
        wolf.setTamed(true);
        wolf.setOwnerId(player.getUniqueID());
        wolf.setCollarColor(getRandomColor());
        return wolf;
    }

    /**
     * Get a random dye color.
     *
     * @return Random color
     */
    public static EnumDyeColor getRandomColor()
    {
        return EnumDyeColor.values()[ThreadLocalRandom.current().nextInt(0, EnumDyeColor.values().length)];
    }
}
