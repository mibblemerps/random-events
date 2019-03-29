package net.mitchfizz05.randomevents.world.biomes;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChunkGeneratorDreamRealm implements IChunkGenerator
{
    private World world;

    public ChunkGeneratorDreamRealm(World world)
    {
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ)
    {
        Chunk chunk = new Chunk(world, chunkX, chunkZ);

        if (chunkX == 0 && chunkZ == 0)
        {
            chunk.setBlockState(new BlockPos(0, 1, 0), Blocks.BEDROCK.getDefaultState());
        }

        // Set biome
        //Biome[] abiome = this.world.getBiomeProvider().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i)
        {
            //biomeArray[i] = (byte)Biome.getIdForBiome(abiome[i]);
            biomeArray[i] = (byte) Biome.getIdForBiome(Biomes.DREAMREALM);
        }

        chunk.generateSkylightMap();

        return chunk;
    }

    @Override
    public void populate(int x, int z)
    {

    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z)
    {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return new ArrayList<>(); // no possible creatures
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        return false;
    }
}
