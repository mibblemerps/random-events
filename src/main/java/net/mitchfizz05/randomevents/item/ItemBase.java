package net.mitchfizz05.randomevents.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.mitchfizz05.randomevents.RandomEvents;

public class ItemBase extends Item
{
    protected String name;

    public ItemBase(String name)
    {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(RandomEvents.MOD_ID, name);

        setCreativeTab(CreativeTabs.MISC);
    }
}
