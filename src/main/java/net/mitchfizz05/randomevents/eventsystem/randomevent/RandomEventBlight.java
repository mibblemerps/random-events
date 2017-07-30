package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Blight - Destroys all the crops in the area.
 */
public class RandomEventBlight extends RandomEvent
{
    private double cropDieChance = 0.93;
    private double poisonPotatoDropChance = 0.2;

    public RandomEventBlight()
    {
        super("blight");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        addComponent(new CPlayerEvent());

        // Load config
        poisonPotatoDropChance = RandomEvents.config.get(getConfigName(), "poison_potato_drop_chance", poisonPotatoDropChance,
                "Chance of a poison potato being dropped when a potato plant is destroyed.",
                0, 1).getDouble();

        cropDieChance = RandomEvents.config.get(getConfigName(), "crop_die_chance", cropDieChance,
                "Chance that a crop will die in a blight.",
                0, 1).getDouble();
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Find all crops nearby
        CoordinateHelper.BlockScanResult[] blockScan = CoordinateHelper.scanForBlocks(world, new CoordinateHelper.ICheckBlock() {
            @Override
            public boolean checkBlock(IBlockState blockState) {
                return blockState.getBlock() instanceof BlockCrops;
            }
        }, player.getPosition(), 128);

        // Loop through crops
        for (CoordinateHelper.BlockScanResult result : blockScan) {
            // Some crops may survive.
            if (ThreadLocalRandom.current().nextDouble() > cropDieChance)
                continue;

            // Destroy crop
            world.setBlockToAir(result.position);

            // If potato, there's a chance we'll drop a poisonous potato in it's place
            if (result.block instanceof BlockPotato) {
                if (ThreadLocalRandom.current().nextDouble() < poisonPotatoDropChance) {
                    world.spawnEntity(new EntityItem(world,
                            result.position.getX(), result.position.getY(), result.position.getZ(),
                            new ItemStack(Items.POISONOUS_POTATO, 1)));
                }
            }
        }

        if (blockScan.length == 0) {
            throw new ExecuteEventException("No crops found", this);
        }
    }
}
