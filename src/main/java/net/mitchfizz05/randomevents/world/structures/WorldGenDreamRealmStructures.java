package net.mitchfizz05.randomevents.world.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;

import java.util.Random;

public class WorldGenDreamRealmStructures implements IWorldGenerator
{
    public static final WorldGenStructure NIGHTMARE_STRUCTURE = new WorldGenStructure("nightmare");

    private static final int xChunkSpacing = 4;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        // Only generate in Dream realm
        if (world.provider.getDimension() != Dimensions.DREAMREALM.getId()) return;

        // Only generate along the zero chunk axis
        if (chunkZ != 0) return;

        // Only generate every x chunks
        if (chunkX % xChunkSpacing != 0) return;

        WorldServer worldServer = world.getMinecraftServer().getWorld(world.provider.getDimension());

        TemplateManager templateManager = worldServer.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(RandomEvents.MOD_ID, "nightmare");

        int x = chunkX * 16;
        int z = chunkZ * 16;
        int y = 64;
        BlockPos pos = new BlockPos(x, y, z);

        PlacementSettings placementSettings = (new PlacementSettings())
                .setChunk(null)
                .setIgnoreEntities(false)
                .setIgnoreStructureBlock(false)
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE);

        Template template = templateManager.get(world.getMinecraftServer(), resourceLocation);

        if (template != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            template.addBlocksToWorldChunk(world, pos, placementSettings);
        }
    }
}
