package net.mitchfizz05.randomevents.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEventNightmare;

public class ItemDreamSword extends ItemSword implements RandomEventNightmare.IDreamItem
{
    public ItemDreamSword()
    {
        super(ToolMaterial.DIAMOND);

        ItemBase.init(this, "dream_sword");
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }
}
