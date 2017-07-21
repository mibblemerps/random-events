package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Finds a significant chunk of wood planks or similar material around the player and set it alight.
 */
public class RandomEventBuildingFire extends RandomEvent
{
    /**
     * Blocks that are considered man-made structural blocks that this event will target to burn down.
     */
    protected List<Block> targetBlocks = new ArrayList<Block>();

    /**
     * Minimum size of the chunk of blocks to be considered for burning.
     * This is to prevent a single wooden plank out in the open catching on fire and calling it a "building fire".
     */
    private int minimumSize = 8;

    /**
     * Max number of times to analyse a found block and see if it is a significant vein.
     */
    private int maxTries = 10;

    public RandomEventBuildingFire()
    {
        super("building_fire");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());

        // Add target blocks
        targetBlocks.add(Blocks.PLANKS);
        targetBlocks.add(Blocks.WOOL);
        targetBlocks.add(Blocks.CARPET);

        // Read config
        minimumSize = RandomEvents.config.get(getConfigName(), "minimum_vein_size", minimumSize,
                "Minimum size of the chunk of blocks to be considered for burning.\n" +
                "This is to prevent a single wooden plank out in the open catching on fire and calling it a \"building fire\".").getInt();

        maxTries = RandomEvents.config.get(getConfigName(), "max_tries", maxTries,
                "Max number of times to analyse a found block and see if it is a significant chunk of blocks worth burning.").getInt();
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Scan for target blocks
        CoordinateHelper.BlockScanResult[] resultArray = CoordinateHelper.scanForBlocks(world, targetBlocks, player.getPosition(), 32);

        // Put into a List
        List<CoordinateHelper.BlockScanResult> results = new ArrayList<CoordinateHelper.BlockScanResult>();
        for (CoordinateHelper.BlockScanResult scanResult : resultArray)
            results.add(scanResult);
        Collections.shuffle(results); // Shuffle list for randomness.

        if (resultArray.length == 0)
            throw new ExecuteEventException("No suitable blocks were found", this);

        CoordinateHelper.BlockScanResult target = null;

        for (int i = 0; i < Math.max(results.size(), maxTries); i++) {
            // Pick random block
            CoordinateHelper.BlockScanResult result = results.get(i);

            // Scan vein
            int veinSize = CoordinateHelper.scanVein(world, result.position, minimumSize);

            // Check size
            if (veinSize >= minimumSize) {
                // Big enough - this will do
                target = result;
                break;
            }
        }

        if (target == null)
            throw new ExecuteEventException("Not enough suitable blocks were found", this);

        // Ignite
        for (BlockPos pos : CoordinateHelper.getCoordinatesAround(target.position, 3)) {
            if (world.isAirBlock(pos) && Blocks.FIRE.canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState()); // set fire!
            }
        }
    }
}
