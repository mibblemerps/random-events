package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

public class RandomEventFurnaceMeltdown extends RandomEvent
{
    public RandomEventFurnaceMeltdown()
    {
        super("furnace_meltdown");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1f), TimeHelper.hrsToSecs(3f)));
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

        // Explode
        world.createExplosion(null, poorFurnace.position.getX(), poorFurnace.position.getY(), poorFurnace.position.getZ(), 4.5f, true);
    }
}
