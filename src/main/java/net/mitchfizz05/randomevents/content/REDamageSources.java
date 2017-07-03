package net.mitchfizz05.randomevents.content;

import net.minecraft.util.DamageSource;

/**
 * Random Events damage sources
 */
public class REDamageSources
{
    public static DamageSource acidRain = new DamageSource("acidrain")
            .setDamageBypassesArmor()
            .setDamageIsAbsolute();

    public static DamageSource plague = new DamageSource("plague")
            .setDamageBypassesArmor()
            .setDamageIsAbsolute();
}
