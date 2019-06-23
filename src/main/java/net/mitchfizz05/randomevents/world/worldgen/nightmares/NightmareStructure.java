package net.mitchfizz05.randomevents.world.worldgen.nightmares;

import net.minecraft.block.BlockSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.mitchfizz05.randomevents.RandomEvents;

import javax.annotation.Nullable;

public class NightmareStructure
{
    private String id;
    private ResourceLocation resource;
    private PlacementSettings placementSettings;
    private Vec3d spawnPos = Vec3d.ZERO;
    private BlockPos endBedPos = new BlockPos(0, 0, 10);

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

    public void placeIntoWorld(WorldServer world, @Nullable EntityPlayer player, BlockPos pos)
    {
        TemplateManager templateManager = world.getStructureTemplateManager();

        Template template = templateManager.get(world.getMinecraftServer(), resource);

        if (template != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            template.addBlocksToWorld(world, pos, placementSettings);

            BlockPos size = template.getSize();
            BlockPos end = pos.add(size.getX(), size.getY(), size.getZ());

            world.tick();
            updateSpecialSigns(world, player, pos, end);

            forceLightingCheck(world, pos, end);
        }
        else
        {
            RandomEvents.logger.fatal("Failed to get template for " + this.id + " nightmare!");
        }
    }

    private void updateSpecialSigns(World world, @Nullable EntityPlayer player, BlockPos start, BlockPos end)
    {
        for (int x = start.getX(); x < end.getX(); x++)
        {
            for (int y = start.getY(); y < end.getX(); y++)
            {
                for (int z = start.getZ(); z < end.getZ(); z++)
                {
                    BlockPos targetPos = new BlockPos(x, y, z);
                    IBlockState blockState = world.getBlockState(targetPos);

                    if (blockState.getBlock() instanceof BlockSign)
                    {
                        TileEntitySign sign = (TileEntitySign) world.getTileEntity(targetPos);
                        if (sign == null) continue;

                        for (int line = 0; line < sign.signText.length; line++)
                        {
                            Style style = sign.signText[line].getStyle();
                            String lineText = sign.signText[line].getUnformattedText();

                            if (lineText.contains("[Forecast]"))
                            {
                                // Not yet supported, destroy sign
                                world.setBlockToAir(targetPos);
                                continue;
                            }

                            if (player != null)
                            {
                                lineText = lineText.replaceAll("\\[PlayerName\\]", player.getDisplayNameString());
                            }

                            sign.signText[line] = new TextComponentString(lineText).setStyle(style);
                        }
                    }
                }
            }
        }
    }

    public static void forceLightingCheck(World world, BlockPos start, BlockPos end)
    {
        for (int x = start.getX(); x < end.getX(); x += 16)
        {
            for (int y = start.getY(); y < end.getX(); y += 16)
            {
                for (int z = start.getZ(); z < end.getZ(); z += 16)
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

    public Vec3d getSpawnPos()
    {
        return spawnPos;
    }

    public NightmareStructure setSpawnPos(Vec3d spawnPos)
    {
        this.spawnPos = spawnPos;
        return this;
    }

    public BlockPos getEndBedPos()
    {
        return endBedPos;
    }

    public NightmareStructure setEndBedPos(BlockPos endBedPos)
    {
        this.endBedPos = endBedPos;
        return this;
    }
}
