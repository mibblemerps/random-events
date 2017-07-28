package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.util.TimeHelper;

/**
 * Handles the event timer speed multiplier.
 */
public class EventTimerMultiplierService
{
    /**
     * The multiplier as configured in the config file.
     */
    private double configuredMultiplier = 1.0;

    public EventTimerMultiplierService()
    {
        // Load config
        configuredMultiplier = RandomEvents.config.get("general", "speed_multiplier", configuredMultiplier,
                "Speed multiplier for event timers. 1 = normal, 2 = events happen twice as fast.").getDouble();
    }

    public double getConfiguredMultiplier()
    {
        return configuredMultiplier;
    }

    /**
     * Get the multiplier calculated from the world difficulty.
     *
     * @param world World
     * @return Multiplier
     */
    public double getDifficultyMultiplier(World world)
    {
        switch (world.getDifficulty()) {
            case PEACEFUL:
                return 0.7;
            case EASY:
                return 0.8;
            case NORMAL:
                return 0.9;
            case HARD:
                return 1;
            default:
                return 1.0;
        }
    }

    /**
     * Get the multiplier calculated from how long the world has existed.
     *
     * @param world World
     * @return Multiplier
     */
    // todo: make this not increase whilst no-one is online.
    public double getWorldExistedMultiplier(World world)
    {
        // Initial time where multiplier doesn't increment
        int noIncrementPeriod = TimeHelper.hrsToTicks(1);

        double multiplier = 1 + (double)(world.getTotalWorldTime() - noIncrementPeriod) / (double)TimeHelper.hrsToTicks(8);

        return Math.max(1.0, multiplier);
    }

    /**
     * If no players are online, set the multiplier to 0 (effectively pausing events).
     *
     * @param world World
     * @return
     */
    public double getPlayersOnlineMultiplier(World world)
    {
        try {
            if (world.getMinecraftServer().getPlayerList().getCurrentPlayerCount() == 0) {
                return 0;
            } else {
                return 1;
            }
        } catch (NullPointerException e) {
            return 1;
        }
    }

    /**
     * Get total multiplier taking all values into account.
     *
     * @param world World
     * @return Multiplier
     */
    public double getMultiplier(World world)
    {
        return getConfiguredMultiplier() *
                getDifficultyMultiplier(world) *
                getWorldExistedMultiplier(world) *
                getPlayersOnlineMultiplier(world);
    }
}
