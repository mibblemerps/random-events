package net.mitchfizz05.randomevents.world.dimension.dreamrealm;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.mitchfizz05.randomevents.world.biomes.Biomes;
import net.mitchfizz05.randomevents.world.biomes.ChunkGeneratorDreamRealm;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;

public class DimensionDreamRealm extends WorldProvider
{
    public DimensionDreamRealm()
    {
        this.biomeProvider = new BiomeProviderSingle(Biomes.DREAMREALM);
    }

    @Override
    public DimensionType getDimensionType()
    {
        return Dimensions.DREAMREALM;
    }

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkGeneratorDreamRealm(world);
    }

    @Override
    public boolean canRespawnHere()
    {
        return true;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }
}
