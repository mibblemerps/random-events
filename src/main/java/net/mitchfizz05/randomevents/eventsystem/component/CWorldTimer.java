package net.mitchfizz05.randomevents.eventsystem.component;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.nbt.NBTTagCompound;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;

/**
 * Runs at a regular interval and triggers on the world (not specific to a player).
 */
public class CWorldTimer implements IComponent, IUsesNBT
{
    public int minWaitTime;
    public int maxWaitTime;

    public int timeElapsed;
    public int targetTime;

    public CWorldTimer(int minWaitTime, int maxWaitTime)
    {
        this.minWaitTime = minWaitTime;
        this.maxWaitTime = maxWaitTime;
    }

    /**
     * Reset the timer
     */
    public void reset()
    {
        timeElapsed = 0;
        targetTime = ThreadLocalRandom.current().nextInt(minWaitTime, maxWaitTime + 1);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("time_elapsed", timeElapsed);
        compound.setInteger("target_time", targetTime);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        timeElapsed = compound.getInteger("time_elapsed");
        targetTime = compound.getInteger("target_time");
    }
}
