package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
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

/**
 * A pink sheep spawns nearby.
 */
public class RandomEventPinkSheep extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    public RandomEventPinkSheep()
    {
        super("pink_sheep");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.GOOD;

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(0.5), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.MobSpawnEventParameters parameters = new MobSpawner.MobSpawnEventParameters(1, 1, 32);
        MobSpawner.execute(this, parameters, world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntitySheep sheep = new EntitySheep(world);
        sheep.setFleeceColor(EnumDyeColor.PINK);
        return sheep;
    }
}
