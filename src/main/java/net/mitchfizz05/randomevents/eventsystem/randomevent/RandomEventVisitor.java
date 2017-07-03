package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
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
 * A villager spawns nearby.
 */
public class RandomEventVisitor extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    public RandomEventVisitor()
    {
        super("visitor");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.GOOD;

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToTicks(0.5f), TimeHelper.hrsToTicks(2)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.MobSpawnEventParameters parameters = new MobSpawner.MobSpawnEventParameters(1, 1, 32);
        MobSpawner.execute(this, parameters, world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntityVillager villager = new EntityVillager(world);
        villager.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 60 * 20));

        return villager;
    }
}
