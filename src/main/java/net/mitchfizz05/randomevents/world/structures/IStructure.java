package net.mitchfizz05.randomevents.world.structures;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;

public interface IStructure
{
    public static final WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);

    public static final PlacementSettings placementSettings = (new PlacementSettings())
            .setChunk(null)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(false)
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE);
}
