package net.mitchfizz05.randomevents.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Simple teleporter that shouldn't spawn portals.
 */
public class SimpleTeleporter extends Teleporter
{
    public SimpleTeleporter(WorldServer world)
    {
        super(world);
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {}

    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) { return false; }

    @Override
    public void removeStalePortalLocations(long worldTime) {}

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {}
}
