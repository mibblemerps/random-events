package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.MobSpawner;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A very slow but powerful zombie spawns nearby.
 */
public class RandomEventTank extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    public RandomEventTank()
    {
        super("tank");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(0.5), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.MobSpawnEventParameters parameters = new MobSpawner.MobSpawnEventParameters(1, 1, 24);
        MobSpawner.execute(this, parameters, world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntityZombie zombie = new EntityZombie(world);
        zombie.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, Integer.MAX_VALUE, 2)); // Slowness III
        zombie.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, Integer.MAX_VALUE, 6)); // Strength VII
        zombie.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, Integer.MAX_VALUE, 9)); // Absorption 10 (40 extra health)
        zombie.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 5)); // Fire Resistance VI

        zombie.setCustomNameTag("Tank Zombie");
        zombie.setAlwaysRenderNameTag(true);

        zombie.setBreakDoorsAItask(true); // Make doors breakable
        zombie.setAttackTarget(player); // Target the player
        zombie.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100f); // Extend follow distance

        return zombie;
    }
}
