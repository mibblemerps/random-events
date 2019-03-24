package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.*;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventCargoDrop extends RandomEvent
{
    private String cargoDropLootTable = "minecraft:chests/simple_dungeon";

    public RandomEventCargoDrop()
    {
        super("cargo_drop");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.VERY_GOOD;

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(0.75), TimeHelper.hrsToSecs(3)));
        addComponent(new CPlayerEvent());

        // Load config
        cargoDropLootTable = RandomEvents.config.get(getConfigName(), "loot_table", cargoDropLootTable,
                "Loot table to use for cargo drops.").getString();
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        BlockPos dropPos = CoordinateHelper.pickPositionAroundPerimeter(world, player.getPosition(), ThreadLocalRandom.current().nextInt(16, 32), null)
                .add(0, -1, 0);

        world.createExplosion(null, dropPos.getX() + 0.5f, dropPos.getY() + 1.5f, dropPos.getZ() + 0.5f, 1f, true);

        world.setBlockState(dropPos, Blocks.CHEST.getDefaultState());
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(dropPos);
        if (chest != null) {
            chest.setLootTable(new ResourceLocation(cargoDropLootTable), ThreadLocalRandom.current().nextLong());
        }
    }
}
