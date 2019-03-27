package net.mitchfizz05.randomevents.world.biomes;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class BiomeDreamRealm extends Biome
{
    public BiomeDreamRealm()
    {
        super(new BiomeProperties("DreamRealm")
                .setTemperature(0.6f)
                .setRainDisabled()
                .setWaterColor(MathHelper.rgb(0, 94, 247)));
    }

    @Override
    public int getSkyColorByTemp(float currentTemperature)
    {
        return MathHelper.rgb(82, 0, 117);
    }
}
