package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomEventTorchFire extends RandomEvent
{
    private int torchScanRadius = 64;
    private int numberOfFireBlocksToCreate = 1;

    public RandomEventTorchFire()
    {
        super("torch_fire");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(0.5), TimeHelper.hrsToSecs(1)));
        addComponent(new CPlayerEvent());

        // Load config
        numberOfFireBlocksToCreate = RandomEvents.config.get(getConfigName(), "number_of_fires", numberOfFireBlocksToCreate,
                "Number of fire blocks are torch fire should create.").getInt();
        torchScanRadius = RandomEvents.config.get(getConfigName(), "torch_scan_radius", torchScanRadius).getInt();
        setEnableAnnouncement(RandomEvents.config.get(getConfigName(), "enable_announcement", false).getBoolean());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Find all torches nearby.
        ArrayList<CoordinateHelper.BlockScanResult> blockScan
                = new ArrayList<>(Arrays.asList(CoordinateHelper.scanForBlocks(world, Blocks.TORCH, player.getPosition(), torchScanRadius)));

        if (blockScan.size() == 0)
            throw new ExecuteEventException("No torches found.", this);

        // Pick one.
        int i = ThreadLocalRandom.current().nextInt(0, blockScan.size());
        BlockPos torchPos = blockScan.get(i).position;

        ArrayList<BlockPos> targets = new ArrayList<>();
        targets.add(torchPos.add(0, 1, 0)); // Above and below the torch are the first places to catch fire
        targets.add(torchPos.add(0, -1, 0));
        targets.add(torchPos.add(1, 0, 0));
        targets.add(torchPos.add(-1, 0, 0));
        targets.add(torchPos.add(0, 0, 1));
        targets.add(torchPos.add(0, 0, -1));

        int j = 0; // number of fires made so far
        for (BlockPos target : targets) {
            if (world.isAirBlock(target) && canNeighborCatchFire(world, target)) {
                if (world.setBlockState(target, Blocks.FIRE.getDefaultState())) {
                    // Successfully lit fire
                    if (++j < numberOfFireBlocksToCreate) {
                        return;
                    }
                }
            }
        }

        if (j == 0) {
            throw new ExecuteEventException("Torch found but no opportunity to create fire.", this, true);
        }
    }

    public static boolean canNeighborCatchFire(World world, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (Blocks.FIRE.canCatchFire(world, pos.offset(enumfacing), enumfacing.getOpposite())) {
                return true;
            }
        }

        return false;
    }
}
