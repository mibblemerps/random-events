package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CRandomPlayer;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Forest fire. Ignites random leaf blocks nearby.
 */
public class RandomEventForestFire extends RandomEvent
{
    public RandomEventForestFire()
    {
        super("forest_fire");

        addComponent(new CWorldTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());
        addComponent(new CRandomPlayer());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        CoordinateHelper.BlockScanResult[] scanResults = CoordinateHelper.scanForBlocks(world, Blocks.LEAVES, player.getPosition(), 64);

        boolean litFire = false;

        Random random = ThreadLocalRandom.current();
        for (CoordinateHelper.BlockScanResult result : scanResults) {
            BlockPos above = new BlockPos(result.position.getX(), result.position.getY() + 1, result.position.getZ());
            if (world.getBlockState(above).getBlock() == Blocks.AIR) {
                // Block is suitable for hosting fire.
                if (random.nextFloat() < 0.0071) { // 0.71% chance.
                    world.setBlockState(above, Blocks.FIRE.getDefaultState()); // Set fire
                    litFire = true;
                }
            }
        }

        if (!litFire) throw new ExecuteEventException("Nowhere found to light fire", this);
    }
}
