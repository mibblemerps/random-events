package net.mitchfizz05.randomevents.block;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class REBlocks {
    public static BlockEvaporatedWater evaporatedWater;

    public static void register()
    {
        evaporatedWater = new BlockEvaporatedWater();

        ForgeRegistries.BLOCKS.register(evaporatedWater);
    }
}
