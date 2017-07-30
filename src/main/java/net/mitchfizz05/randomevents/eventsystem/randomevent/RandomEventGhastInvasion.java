package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A ghast spawns from a nether portal.
 */
public class RandomEventGhastInvasion extends RandomEvent {
    public RandomEventGhastInvasion() {
        super("ghast_invasion");
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException {
        super.execute(world, player);

        CoordinateHelper.BlockScanResult[] results = CoordinateHelper.scanForBlocks(world, Blocks.PORTAL,
                player.getPosition(), 96);

        if (results.length == 0)
            throw new ExecuteEventException("No portals found nearby", this);

        // Pick a portal to target
        BlockPos targetPortal = results[ThreadLocalRandom.current().nextInt(0, results.length)].position;

        // Get area around the portal that ghasts can spawn
        List<BlockPos> portalSpawnArea = Arrays.asList(CoordinateHelper.getCoordinatesAround(targetPortal, 5));

        int ghastsToSpawn = ThreadLocalRandom.current().nextInt(1, 3);

        for (int i = 0; i < ghastsToSpawn; i++) {
            // Shuffle the portal spawn area so that the ghast will spawn in different locations, rather than just the first suitable position found
            Collections.shuffle(portalSpawnArea);

            // Try to find a suitable position
            boolean spawned = false;
            for (BlockPos pos : portalSpawnArea) {
                if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.IN_AIR, world, pos)) {
                    // Spawn ghast
                    EntityGhast ghast = new EntityGhast(world);
                    ghast.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    spawned = world.spawnEntity(ghast);

                    break;
                }
            }

            if (!spawned) {
                // Failed to spawn first ghast - give up here
                if (i == 0) {
                    // Since not even 1 ghast has spawned yet, consider the event a failure
                    throw new ExecuteEventException("Couldn't spawn ghasts", this);
                } else {
                    // Since at least 1 ghast has spawned, this isn't a total failure, so don't throw an ExecuteEventException
                    break;
                }
            }
        }
    }
}
