package net.mitchfizz05.randomevents.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TeleportHelper
{
    public static void teleport(Entity entity, float x, float y, float z, float yaw, float pitch, int dim)
    {
        if (entity.world.isRemote) return;

        entity.changeDimension(dim, new SimpleTeleporter(entity.world.getMinecraftServer().getWorld(dim)));

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            player.cameraYaw = yaw;
            player.cameraPitch = pitch;
        }

        entity.setPositionAndUpdate(x, y, z);
    }
}
