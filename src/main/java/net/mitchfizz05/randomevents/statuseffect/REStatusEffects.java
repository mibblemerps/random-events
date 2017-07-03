package net.mitchfizz05.randomevents.statuseffect;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Random Event's status effects
 */
public class REStatusEffects
{
    public static PotionPlague plague = new PotionPlague();
    public static PotionMalaria malaria = new PotionMalaria();

    // ---

    public static void register()
    {
        ForgeRegistries.POTIONS.register(plague);
        ForgeRegistries.POTIONS.register(malaria);
    }
}
