package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
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
 * A pack of angry wolves spawn and target the player.
 */
public class RandomEventHunt extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    public RandomEventHunt()
    {
        super("hunt");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.VERY_BAD;

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.MobSpawnEventParameters parameters = new MobSpawner.MobSpawnEventParameters(5, 9, 32);
        MobSpawner.execute(this, parameters, world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntityWolf wolf = new EntityWolf(world);
        wolf.setRevengeTarget(player); // Attack player
        wolf.setAttackTarget(player);
        wolf.setAngry(true);
        wolf.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80f); // Extend follow distance

        return wolf;
    }
}
