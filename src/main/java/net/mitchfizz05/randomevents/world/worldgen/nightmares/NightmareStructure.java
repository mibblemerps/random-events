package net.mitchfizz05.randomevents.world.worldgen.nightmares;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.mitchfizz05.randomevents.RandomEvents;

public class NightmareStructure
{
    private String id;
    private ResourceLocation resource;
    private PlacementSettings placementSettings;
    private BlockPos spawnPos = BlockPos.ORIGIN;
    private BlockPos endPos = new BlockPos(0, 0, 10);

    public NightmareStructure(String id)
    {
        this.id = id;

        this.resource = new ResourceLocation(RandomEvents.MOD_ID, "nightmares/" + id);

        this.placementSettings = (new PlacementSettings())
                .setChunk(null)
                .setIgnoreEntities(false)
                .setIgnoreStructureBlock(false)
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE);
    }

    public void placeIntoWorld(WorldServer world, BlockPos pos)
    {
        TemplateManager templateManager = world.getStructureTemplateManager();

        Template template = templateManager.get(world.getMinecraftServer(), resource);

        if (template != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            template.addBlocksToWorld(world, pos, placementSettings);

            forceLightingCheck(world, pos, template.getSize());
        }
        else
        {
            RandomEvents.logger.fatal("Failed to get template for " + this.id + " nightmare!");
        }
    }

    public static void forceLightingCheck(World world, BlockPos pos, BlockPos size)
    {
        BlockPos end = pos.add(size.getX(), size.getY(), size.getZ());

        for (int x = pos.getX(); x < end.getX(); x += 16)
        {
            for (int y = pos.getY(); y < end.getX(); y += 16)
            {
                for (int z = pos.getZ(); z < end.getZ(); z += 16)
                {
                    world.getChunkFromBlockCoords(new BlockPos(x, y, z)).checkLight();
                }
            }
        }
    }

    public String getLocalizedName()
    {
        return I18n.format("nightmare." + id + ".name");
    }

    public String getId()
    {
        return id;
    }

    public ResourceLocation getResource()
    {
        return resource;
    }

    public NightmareStructure setResource(ResourceLocation resource)
    {
        this.resource = resource;
        return this;
    }

    public PlacementSettings getPlacementSettings()
    {
        return placementSettings;
    }

    public NightmareStructure setPlacementSettings(PlacementSettings placementSettings)
    {
        this.placementSettings = placementSettings;
        return this;
    }

    public BlockPos getSpawnPos()
    {
        return spawnPos;
    }

    public NightmareStructure setSpawnPos(BlockPos spawnPos)
    {
        this.spawnPos = spawnPos;
        return this;
    }

    public BlockPos getEndPos()
    {
        return endPos;
    }

    public NightmareStructure setEndPos(BlockPos endPos)
    {
        this.endPos = endPos;
        return this;
    }
}
