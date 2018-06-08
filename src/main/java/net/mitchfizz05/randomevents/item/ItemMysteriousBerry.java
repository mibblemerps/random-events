package net.mitchfizz05.randomevents.item;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mitchfizz05.randomevents.statuseffect.REStatusEffects;
import net.mitchfizz05.randomevents.util.TimeHelper;

import java.util.List;

/**
 * A berry that has various random possible effects.
 */
public class ItemMysteriousBerry extends ItemFood
{
    public static BerryType[] berryTypeValues = BerryType.values();

    public ItemMysteriousBerry()
    {
        super(0, 0, false);
        ItemBase.init(this, "mysterious_berry");

        setAlwaysEdible();
        setHasSubtypes(true);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
    {
        super.onFoodEaten(stack, world, player);

        if (!world.isRemote) {
            useBerry(getBerryType(stack, true), world, player);
            setDiscovered(stack, true);
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(I18n.format("item.mysterious_berry.tooltip"));
        if (getDiscovered(stack)) {
            // Berry discovered - add berry type to tooltip
            tooltip.add(I18n.format("item.mysterious_berry." + getBerryType(stack, false)));
        } else {
            // Berry not discovered - show undiscovered tooltip
            tooltip.add(I18n.format("item.mysterious_berry.undiscovered"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (tab != getCreativeTab()) return;

        // Add each berry type as a sub item.
        for (BerryType type : berryTypeValues) {
            subItems.add(getBerryItemStack(type, true));
        }
    }

    // Make the berry look all shiny x3
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    /**
     * Perform the berries effect.
     *
     * @param type Berry type
     * @param world World
     * @param player Player
     */
    protected void useBerry(BerryType type, World world, EntityPlayer player)
    {
        if (type == BerryType.MALARIA) {
            // Malaria
            player.addPotionEffect(new PotionEffect(REStatusEffects.malaria, TimeHelper.minsToTicks(8), 0));
        } else if (type == BerryType.PLAGUE) {
            // Plague
            player.addPotionEffect(new PotionEffect(REStatusEffects.plague, REStatusEffects.plague.defaultDuration));
        } else if (type == BerryType.REGENERATION) {
            // Regeneration
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, TimeHelper.secsToTicks(30), 0));
        } else if (type == BerryType.MEDICINE) {
            // Medicate the player
            ItemMedicalPack.TreatResult treatResult = ItemMedicalPack.treat(player, 0.5f);
            if (treatResult.treatmentSuccessful && treatResult.wasTreatmentAttempted) {
                player.sendMessage(new TextComponentTranslation("medicine.treat.success", treatResult.effect.getName()));
            } else if (treatResult.wasTreatmentAttempted) {
                player.sendMessage(new TextComponentTranslation("medicine.treat.failure", treatResult.effect.getName()));
            } else {
                player.sendMessage(new TextComponentTranslation("medicine.treat.nothing"));
            }
        }
    }

    /**
     * Get an {@link ItemStack} for a berry of a particular type.
     *
     * @param type Berry type
     * @param discovered Should the berry be "discovered" (it's type known)?
     * @return Berry (with type set)
     */
    protected ItemStack getBerryItemStack(BerryType type, boolean discovered)
    {
        ItemStack stack = new ItemStack(this);
        setBerryType(stack, type);
        setDiscovered(stack, discovered);
        return stack;
    }

    /**
     * Get the berry type.
     * If the berry has no type NBT tag set, it'll generate random type for this itemstack.
     *
     * @param stack Berry
     * @return Berry type
     */
    public BerryType getBerryType(ItemStack stack, boolean allowGenerating)
    {
        // Check if the berry has a type, if not, pick a random one.
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("berry_type")) {
            // Berry has no type and we're not allowed to generate it on the spot. Just assume it's a dud.
            if (!allowGenerating)
                return BerryType.DUD;

            setRandomBerryType(stack);
        }

        String typeName = stack.getTagCompound().getString("berry_type");
        return BerryType.valueOf(typeName);
    }

    /**
     * Set a new berry type.
     *
     * @param stack Berry
     * @param type New berry type
     */
    public void setBerryType(ItemStack stack, BerryType type)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        if (stack.hasTagCompound())
            nbt = stack.getTagCompound();

        nbt.setString("berry_type", type.toString());

        stack.setTagCompound(nbt);
    }

    /**
     * Set berry to a random berry type.
     *
     * @param stack Berry to set random type to
     * @return The berry type that was chosen
     */
    public BerryType setRandomBerryType(ItemStack stack)
    {
        BerryType newType = berryTypeValues[ThreadLocalRandom.current().nextInt(berryTypeValues.length)];
        setBerryType(stack, newType);

        return newType;
    }

    /**
     * Has this berry had it's type discovered yet?
     *
     * @param stack Berry
     * @return Is discovered?
     */
    public boolean getDiscovered(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            return false;

        NBTTagCompound nbt = stack.getTagCompound();

        if (!nbt.hasKey("discovered"))
            return false;

        return nbt.getBoolean("discovered");
    }

    /**
     * Set whether this berry has been discoverd yet.
     *
     * @param stack Berry
     * @param discovered Is discovered?
     */
    public void setDiscovered(ItemStack stack, boolean discovered)
    {
        // This is just to ensure the item has an NBT tag and a set berry type
        getBerryType(stack, true);

        NBTTagCompound nbt = stack.getTagCompound();
        nbt.setBoolean("discovered", discovered);
    }

    public enum BerryType
    {
        DUD,
        PLAGUE,
        MALARIA,
        REGENERATION,
        MEDICINE
    }
}
