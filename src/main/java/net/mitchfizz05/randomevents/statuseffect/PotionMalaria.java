package net.mitchfizz05.randomevents.statuseffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.mitchfizz05.randomevents.RandomEvents;

/**
 * Malaria health effect.
 *
 * TODO: add icon
 */
public class PotionMalaria extends Potion implements ITreatableWithMedicine
{
    AttributeModifier slownessAttributeModifier = new AttributeModifier("malaria_slowness", -0.20D, 2);

    public PotionMalaria()
    {
        super(false, 0xc8f442);
        setRegistryName(RandomEvents.MOD_ID, "malaria");
        setPotionName("effect.randomevents.malaria");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int p_76394_2_)
    {
        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect == null) {
            RandomEvents.logger.warn("Attempt to get active PotionEffect failed (got null).");
            return;
        }

        int duration = effect.getDuration() / 20; // seconds
        int amp = effect.getAmplifier();

        if (duration <= 300) {
            // 5 minute mark
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30, amp, false, false));

            if (duration <= 120) {
                // 2 minute mark
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, amp, false, false));

                if (duration <= 90) {
                    // 1.5 minute mark
                    entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30, Math.max(amp + 2, 4), false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, Math.max(amp + 1, 3), false, false));

                    if (duration <= 30) {
                        // 30 second mark (imminent death)
                        entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 5, 0, false, false));

                        // Hurt entity
                        int k = 16 >> amp;
                        if (k <= 0 || effect.getDuration() % k == 0) {
                            entity.attackEntityFrom(DamageSource.GENERIC, 2f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return true;
    }

    @Override
    public float getTreatChance()
    {
        return 0.75f;
    }
}
