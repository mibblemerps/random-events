package net.mitchfizz05.randomevents.item;

import io.netty.util.internal.ThreadLocalRandom;
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

        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
        potionEffects.addAll(player.getActivePotionEffects());

        Collections.shuffle(potionEffects, ThreadLocalRandom.current());

        for (PotionEffect effect : potionEffects) {
            Potion potion = effect.getPotion();

            if (potion instanceof ITreatableWithMedicine) {
                // Treat this effect.
                float treatChance = ((ITreatableWithMedicine) potion).getTreatChance();
                if (ThreadLocalRandom.current().nextFloat() < treatChance) {
                    // Successful treatment
                    player.sendMessage(new TextComponentTranslation("item.medical_pack.treat.success", potion.getName()));
                    player.removePotionEffect(potion);
                } else {
                    // Failed treatment
                    player.sendMessage(new TextComponentTranslation("item.medical_pack.treat.failure", potion.getName()));
                }

                // Consume medicine
                itemStack.shrink(1);

                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
    }
}
