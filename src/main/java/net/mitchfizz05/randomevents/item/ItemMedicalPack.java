package net.mitchfizz05.randomevents.item;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.statuseffect.ITreatableWithMedicine;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A medical pack to cure various health conditions.
 */
public class ItemMedicalPack extends ItemBase
{
    public ItemMedicalPack() {
        super("medical_pack");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
    {
        ItemStack itemStack = player.getHeldItem(handIn);

        if (worldIn.isRemote)
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);

        TreatResult treatResult = treat(player, 1);

        if (treatResult.treatmentSuccessful && treatResult.wasTreatmentAttempted) {
            itemStack.shrink(1);
            player.sendMessage(new TextComponentTranslation("medicine.treat.success", treatResult.effect.getName()));
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        } else if (treatResult.wasTreatmentAttempted) {
            player.sendMessage(new TextComponentTranslation("medicine.treat.failure", treatResult.effect.getName()));
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        } else {
            player.sendMessage(new TextComponentTranslation("medicine.treat.nothing"));
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
        }
    }

    /**
     * Treat an entity with medicine.
     *
     * @param entity Entity to treat
     * @param treatMultiplier Treat chance multiplier
     * @return Was the medicine used? This will only return false if there is nothing to treat.
     */
    public static TreatResult treat(EntityLivingBase entity, float treatMultiplier)
    {
        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
        potionEffects.addAll(entity.getActivePotionEffects());

        Collections.shuffle(potionEffects, ThreadLocalRandom.current());

        for (PotionEffect effect : potionEffects) {
            Potion potion = effect.getPotion();

            if (potion instanceof ITreatableWithMedicine) {
                // Treat this effect.
                float treatChance = ((ITreatableWithMedicine) potion).getTreatChance() * treatMultiplier;
                if (ThreadLocalRandom.current().nextFloat() < treatChance) {
                    // Successful treatment
                    entity.removePotionEffect(potion);
                    return new TreatResult(true, true, potion);
                } else {
                    // Failed treatment
                    return new TreatResult(false, true, potion);
                }
            }
        }

        return new TreatResult(false, false, null);
    }

    /**
     * Result of a treatment attempt.
     */
    public static class TreatResult
    {
        /**
         * Was a treatable effect removed by the medicine?
         */
        public boolean treatmentSuccessful;

        /**
         * Was the medicine used to <em>try</em> and treat an effect? True even if the treatment failed.
         * Only false if there was nothing to treat.
         */
        public boolean wasTreatmentAttempted;

        /**
         * The effect that was attempted to be treated (if any).
         */
        @Nullable
        public Potion effect = null;

        public TreatResult(boolean treatmentSuccessful, boolean wasTreatmentAttempted, Potion effect)
        {
            this.treatmentSuccessful = treatmentSuccessful;
            this.wasTreatmentAttempted = wasTreatmentAttempted;
            this.effect = effect;
        }
    }
}
