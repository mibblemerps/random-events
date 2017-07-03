package net.mitchfizz05.randomevents.eventsystem.component;

import net.minecraft.nbt.NBTTagCompound;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;

/**
 * Component for events that last over a period of time.
 */
public class CLongTimedEvent extends CLongEvent implements IUsesNBT
{
    /**
     * Seconds until the event ends.
     */
    public int timeLeft;

    @Override
    public boolean isActive()
    {
        return timeLeft > 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("time_left", timeLeft);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        timeLeft = nbt.getInteger("time_left");
    }
}
