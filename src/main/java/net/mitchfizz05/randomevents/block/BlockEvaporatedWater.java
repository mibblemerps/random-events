package net.mitchfizz05.randomevents.block;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;

import java.util.Random;

public class BlockEvaporatedWater extends BlockAir {
    public BlockEvaporatedWater() {
        setUnlocalizedName("evaporated_water");
        setRegistryName(RandomEvents.MOD_ID, getUnlocalizedName());
        setCreativeTab(CreativeTabs.MISC);

        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (!RandomEvents.weatherHelper.shouldWaterEvaporate()) {
            // Water shouldn't be evaporated - so turn back into water.
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
        }
    }
}
