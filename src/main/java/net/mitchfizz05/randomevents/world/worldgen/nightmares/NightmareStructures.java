package net.mitchfizz05.randomevents.world.worldgen.nightmares;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class NightmareStructures
{
    public static ArrayList<NightmareStructure> nightmareStructures = new ArrayList<>();

    static
    {
        nightmareStructures.add(new NightmareStructure("hedge_maze")
                .setSpawnPos(new BlockPos(15, 4, 15))
                .setEndPos(new BlockPos(0, 4, 16)));
    }
}
