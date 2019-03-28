package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Furnace lights fire around it
 */
public class RandomEventFurnaceFire extends RandomEvent
{
    public RandomEventFurnaceFire()
    {
        super("furnace_fire");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(0.5f), TimeHelper.hrsToSecs(5f)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Find all furnaces nearby.
        ArrayList<CoordinateHelper.BlockScanResult> blockScan = new ArrayList<CoordinateHelper.BlockScanResult>();
        //blockScan.addAll(Arrays.asList(scanForBlocks(world, Blocks.FURNACE, player.getPosition(), 128)));
        blockScan.addAll(Arrays.asList(CoordinateHelper.scanForBlocks(world, Blocks.LIT_FURNACE, player.getPosition(), 128)));

        if (blockScan.size() == 0)
            throw new ExecuteEventException("No active furnaces found", this);

        // Pick one.
        int i = ThreadLocalRandom.current().nextInt(0, blockScan.size());
        CoordinateHelper.BlockScanResult poorFurnace = blockScan.get(i);


        // Set all adjacent blocks on fire.
        for (BlockPos pos : CoordinateHelper.getCoordinatesAround(poorFurnace.position, 2)) {
            if (world.isAirBlock(pos) && Blocks.FIRE.canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState()); // set fire!
            }
        }
    }
}
