package net.mitchfizz05.randomevents.util;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.util.CoordinateHelper;

import java.util.ArrayList;

/**
 * Helper class for events that spawn mob(s) randomly around the player.
 */
public class MobSpawner
{
    /**
     * Spawn entities around the player.
     *
     * @param mobSpawnEvent Random randomevent that implements {@link IMobSpawnEvent}.
     * @param parameters Spawn parameters (min count, max count, radius).
     * @param world World
     * @param player Player to spawn mobs around
     * @return List of spawned entities.
     */
    public static ArrayList<Entity> execute(IMobSpawnEvent mobSpawnEvent, MobSpawnEventParameters parameters, World world, EntityPlayer player)
    {
        int mobCount = ThreadLocalRandom.current().nextInt(parameters.minCount, parameters.maxCount + 1);

        ArrayList<Entity> spawnedEntities = new ArrayList<Entity>();
        for (int i = 0; i < mobCount; i++) {
            spawnedEntities.add(doSpawn(mobSpawnEvent, parameters, world, player));
        }

        return spawnedEntities;
    }

    protected static Entity doSpawn(IMobSpawnEvent mobSpawnEvent, MobSpawnEventParameters parameters, World world, EntityPlayer player)
    {
        // Get a IChecksPickedCoordinates if it's there
        CoordinateHelper.IChecksPickedCoordinates checksPickedCoordinates = null;
        if (mobSpawnEvent instanceof CoordinateHelper.IChecksPickedCoordinates)
            checksPickedCoordinates = (CoordinateHelper.IChecksPickedCoordinates) mobSpawnEvent;

        // Pick position
        BlockPos blockPos = CoordinateHelper.pickPositionAroundPerimeter(world, player.getPosition(), parameters.radius, checksPickedCoordinates);

        // Spawn entity
        Entity entity = mobSpawnEvent.getEntity(world, player);
        entity.setLocationAndAngles(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f, 0f, 0f);
        world.spawnEntity(entity);

        return entity;
    }

    /**
     * MobSpawner randomevent parameters.
     */
    public static class MobSpawnEventParameters
    {
        public int minCount;
        public int maxCount;
        public int radius;

        public MobSpawnEventParameters(int minCount, int maxCount, int radius)
        {
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.radius = radius;
        }
    }

    /**
     * MobSpawner events must implement this interface.
     */
    public interface IMobSpawnEvent
    {
        Entity getEntity(World world, EntityPlayer player);
    }
}
