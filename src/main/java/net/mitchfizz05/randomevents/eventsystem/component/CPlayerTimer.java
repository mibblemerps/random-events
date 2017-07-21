package net.mitchfizz05.randomevents.eventsystem.component;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.IUsesConfig;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Runs at a regular interval and is specific to each player.
 */
public class CPlayerTimer implements IComponent, IUsesNBT, IUsesConfig
{
    protected RandomEvent randomEvent;

    public int minWaitTime;
    public int maxWaitTime;

    private Map<UUID, CWorldTimer> playerTimers = new HashMap<UUID, CWorldTimer>();

    public CPlayerTimer(RandomEvent randomEvent, int minWaitTime, int maxWaitTime)
    {
        this.randomEvent = randomEvent;
        this.minWaitTime = minWaitTime;
        this.maxWaitTime = maxWaitTime;
    }

    public CWorldTimer getTimer(UUID uuid)
    {
        if (!playerTimers.containsKey(uuid)) {
            playerTimers.put(uuid, new CWorldTimer(randomEvent, minWaitTime, maxWaitTime));
        }

        return playerTimers.get(uuid);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        // Loop through all player timers.
        for (UUID uuid : playerTimers.keySet()) {
            CWorldTimer timer = playerTimers.get(uuid);

            // Get the NBT tag for this player's timer.
            NBTTagCompound playerNbt = RandomEventServices.nbtService.getPlayerNbt(uuid, nbt);

            timer.writeToNBT(playerNbt);
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        // Loop through all players in the NBT.
        for (String key : nbt.getKeySet()) {
            try {
                // Attempt to parse to a UUID
                UUID uuid = UUID.fromString(key);

                // Load the timer for this player
                CWorldTimer timer = getTimer(uuid); // This will also add the timer to playerTimers
                timer.readFromNBT(nbt);
            } catch (IllegalArgumentException e) {
                // That key wasn't a player UUID.
            }
        }
    }

    @Override
    public void readConfig(Configuration config)
    {
        this.minWaitTime = config.get(randomEvent.getConfigName(), "min_wait_time", minWaitTime,
                "Minimum amount of time (in seconds) that must pass before this event will trigger").getInt();
        this.maxWaitTime = config.get(randomEvent.getConfigName(), "max_wait_time", maxWaitTime,
                "Maximum amount of time (in seconds) that can pass until this event will trigger").getInt();
    }
}
