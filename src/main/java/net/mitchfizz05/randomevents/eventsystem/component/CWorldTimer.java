package net.mitchfizz05.randomevents.eventsystem.component;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.mitchfizz05.randomevents.eventsystem.IUsesConfig;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

/**
 * Runs at a regular interval and triggers on the world (not specific to a player).
 */
public class CWorldTimer implements IComponent, IUsesNBT, IUsesConfig
{
    protected RandomEvent randomEvent;

    public int minWaitTime;
    public int maxWaitTime;

    public int timeElapsed;
    public int targetTime;

    public CWorldTimer(RandomEvent randomEvent, int minWaitTime, int maxWaitTime)
    {
        this.randomEvent = randomEvent;
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

    @Override
    public void readConfig(Configuration config)
    {
        this.minWaitTime = config.get(randomEvent.getConfigName(), "min_wait_time", minWaitTime,
                "Minimum amount of time (in ticks) that must pass before this event will trigger").getInt();
        this.maxWaitTime = config.get(randomEvent.getConfigName(), "max_wait_time", maxWaitTime,
                "Maximum amount of time (in ticks) that can pass until this event will trigger").getInt();
    }
}
