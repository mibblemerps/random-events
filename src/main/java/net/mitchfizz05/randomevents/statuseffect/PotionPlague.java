package net.mitchfizz05.randomevents.statuseffect;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.content.REDamageSources;
import net.mitchfizz05.randomevents.util.TimeHelper;

import java.util.List;

/**
 * Potion that will slowly reveal symptoms and eventually kill it's host, and worst of all, is highly contagious.
 */
public class PotionPlague extends Potion implements ITreatableWithMedicine
{
    /**
     * Base distance the potion can spread.
     */
    public float baseContagiousDistance = 6f;
    /**
     * A modifier to affect the infection chance.
     * This means if you're directly next to an infected person you're not <b>guaranteed</b> to get infected.
     */
    public float chanceModifier = 0.75f;
    /**
     * How frequently should the potion attempt to infect nearby entities? (In ticks)
     * Too often will cause lag.
     * Make sure to adjust the chance to reflect the frequency.
     */
    public int infectInterval = 100;
    /**
     * Default {@link PotionEffect} duration of the plague. This is the duration that will be applied to other entities
     * that are infected by it.
     */
    public int defaultDuration = TimeHelper.minsToTicks(12); // 12 minutes

    protected float contagiousDistance;

    public PotionPlague()
    {
        super(false, 0xc8f442);
        setRegistryName(RandomEvents.MOD_ID, "plague");
        setPotionName("effect.randomevents.plague");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int p_76394_2_)
    {
        // Only execute on server.
        if (entity.world.isRemote)
            return;

        beContagious(entity);

        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect == null) {
            RandomEvents.logger.warn("Attempt to get active PotionEffect failed (got null).");
            return;
        }

        int duration = effect.getDuration() / 20; // seconds
        int amp = effect.getAmplifier();

        // Constant weakness
        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30, amp, false, false));

        if (duration <= 600) {
            // 10 minute mark
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, amp, false, false));

            if (duration <= 150) {
                // 2.5 minute mark
                entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30, Math.max(amp + 2, 4), false, false));
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, Math.max(amp + 1, 3), false, false));

                if (duration <= 30) {
                    // 30 second mark (imminent death)
                    entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 5, 0, false, false));

                    // Hurt entity
                    int k = 16 >> amp;
                    if (k <= 0 || effect.getDuration() % k == 0) {
                        entity.attackEntityFrom(REDamageSources.plague, 2f);
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

    protected float getContagiousDifficultyMultiplier(EnumDifficulty difficulty)
    {
        switch (difficulty) {
            case PEACEFUL:
                return 0.5f;
            case EASY:
                return 0.75f;
            case NORMAL:
                return 1f;
            case HARD:
                return 1.5f;
        }
        return 1f;
    }

    protected void beContagious(EntityLivingBase entity)
    {
        // Only infect once per interval.
        if (entity.ticksExisted % infectInterval != 0)
            return;

        World world = entity.world;

        contagiousDistance = baseContagiousDistance * getContagiousDifficultyMultiplier(world.getDifficulty());

        AxisAlignedBB spreadRange = new AxisAlignedBB(
                entity.posX - contagiousDistance, entity.posY - contagiousDistance, entity.posZ - contagiousDistance,
                entity.posX + contagiousDistance, entity.posY + contagiousDistance, entity.posZ + contagiousDistance);

        List<EntityLivingBase> canidates = world.getEntitiesWithinAABB(EntityLivingBase.class, spreadRange);

        for (EntityLivingBase canidate : canidates) {
            // Can't spread to self
            if (canidate == entity)
                continue;

            // Already infected.
            if (canidate.isPotionActive(this))
                continue;;

            // Chance of infection is linear to distance away.
            float distance = entity.getDistanceToEntity(canidate);
            float chance = ((contagiousDistance - distance) / contagiousDistance) * chanceModifier;

            // Check if the entity is too far away. This can happen because we're infecting a circular radius but we use a box to get nearby entities.
            if (distance > contagiousDistance)
                continue;


            // Roll the dice
            if (chance > ThreadLocalRandom.current().nextFloat()) {
                // Ensure LOS between the entities.
                RayTraceResult result = world.rayTraceBlocks(
                        entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0),
                        canidate.getPositionVector().addVector(0, entity.getEyeHeight(), 0),
                        true);
                if (result != null) {
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK)
                        continue;
                }


                // Infect.
                canidate.addPotionEffect(new PotionEffect(this, defaultDuration));
                canidate.sendMessage(new TextComponentTranslation("randomevent.plague.infected", entity.getName()));
            }
        }
    }

    @Override
    public float getTreatChance()
    {
        return 0.9f;
    }
}
