package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

/**
 * Blight - Destroys all the crops in the area.
 */
public class RandomEventBlight extends RandomEvent
{
    public RandomEventBlight()
    {
        super("blight");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Find all wheat nearby.
        CoordinateHelper.BlockScanResult[] blockScan = CoordinateHelper.scanForBlocks(world, Blocks.WHEAT, player.getPosition(), 128);

        // Destroy it.
        for (CoordinateHelper.BlockScanResult result : blockScan) {
            world.setBlockToAir(result.position);
        }

        if (blockScan.length == 0) {
            throw new ExecuteEventException("No crops found", this);
        }
    }
}
