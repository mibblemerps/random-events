package net.mitchfizz05.randomevents.world.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.mitchfizz05.randomevents.RandomEvents;

public class Biomes
{
    public static final Biome DREAMREALM = new BiomeDreamRealm();

    public static void register()
    {
        addBiome(DREAMREALM, "DreamRealm", BiomeManager.BiomeType.WARM, BiomeDictionary.Type.VOID, BiomeDictionary.Type.SPOOKY);
    }

    public static Biome addBiome(Biome biome, String name, BiomeManager.BiomeType biomeType, BiomeDictionary.Type... types)
    {
        biome.setRegistryName(name);
        ForgeRegistries.BIOMES.register(biome);

        BiomeDictionary.addTypes(biome, types);
        BiomeManager.addBiome(biomeType, new BiomeManager.BiomeEntry(biome, 10));
        BiomeManager.addSpawnBiome(biome);

        RandomEvents.logger.info("Registered biome: " + biome.getBiomeName());

        return biome;
    }
}
