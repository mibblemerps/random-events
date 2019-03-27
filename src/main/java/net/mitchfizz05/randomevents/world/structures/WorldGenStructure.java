package net.mitchfizz05.randomevents.world.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.mitchfizz05.randomevents.RandomEvents;

import java.util.Random;

public class WorldGenStructure extends WorldGenerator implements IStructure
{
    public String name;

    public WorldGenStructure(String name)
    {
        this.name = name;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position)
    {
        generateStructure(world, position);

        return false;
    }

    public void generateStructure(World world, BlockPos pos)
    {
        MinecraftServer server = world.getMinecraftServer();

        TemplateManager templateManager = worldServer.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(RandomEvents.MOD_ID, this.name);

        Template template = templateManager.get(server, resourceLocation);

        if (template != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            template.addBlocksToWorldChunk(world, pos, placementSettings);
        }
    }
}
