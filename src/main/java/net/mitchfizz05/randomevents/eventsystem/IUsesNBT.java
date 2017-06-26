package net.mitchfizz05.randomevents.eventsystem;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for objects that need to save/load data from nbt.
 */
public interface IUsesNBT
{
    /**
     * Where you should write data to NBT.
     *
     * @param compound Tag to write data to
     * @return The tag after modification
     */
    NBTTagCompound writeToNBT(NBTTagCompound compound);

    /**
     * Load data from NBT.
     *
     * @param compound Tag to read data from
     */
    void readFromNBT(NBTTagCompound compound);
}
