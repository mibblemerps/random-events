package net.mitchfizz05.randomevents.world.worldgen.nightmares;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class NightmareStructures
{
    public static HashMap<String, NightmareStructure> nightmareStructures = new HashMap<>();

    static
    {
        add(new NightmareStructure("hedge_maze")
                .setSpawnPos(new Vec3d(13.5, 4, 13.5))
                .setEndBedPos(new BlockPos(14, 3, 1)));
    }

    private static void add(NightmareStructure structure)
    {
        nightmareStructures.put(structure.getId(), structure);
    }
}
