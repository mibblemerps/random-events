package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.RandomEventsWorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides saving and loading of component data.
 */
public class NbtService
{
    public static final String tagname = "random_events";

    /**
     * The current world saved data provider.
     */
    public RandomEventsWorldSavedData worldSavedData;

    public NbtService()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Get a player-specific NBT compound tag.
     * This is a helper to ensure player-specific data is stored in a consistent compound tag across different services.
     * If the player doesn't have an NBT tag, one will be automatically created.
     *
     * @param uuid Player UUID. Can be obtained from <code>player.getUniqueID()</code>.
     * @param nbt The root NBT Tag to get a player NBT tag from
     * @return Player NBT compound tag
     */
    public NBTTagCompound getPlayerNbt(UUID uuid, NBTTagCompound nbt)
    {
        // Get/create players compound tag
        if (!nbt.hasKey("players"))
            nbt.setTag("players", new NBTTagCompound());
        NBTTagCompound playersNbt = nbt.getCompoundTag("players");

        // Create the player's tag if it doesn't exist
        if (!playersNbt.hasKey(uuid.toString()))
            playersNbt.setTag(uuid.toString(), new NBTTagCompound());

        // Return the player's tag
        return playersNbt.getCompoundTag(uuid.toString());
    }

    public HashMap<UUID, NBTTagCompound> getPlayersNbtMap(NBTTagCompound nbt)
    {
        // Get/create players compound tag
        if (!nbt.hasKey("players"))
            nbt.setTag("players", new NBTTagCompound());
        NBTTagCompound playersNbt = nbt.getCompoundTag("players");

        HashMap<UUID, NBTTagCompound> nbtMap = new HashMap<>();

        // Iterate over all tags
        for (String playerUuidStr : playersNbt.getKeySet()) {
            try {
                UUID playerUuid = UUID.fromString(playerUuidStr);
                nbtMap.put(playerUuid, playersNbt.getCompoundTag(playerUuidStr));
            } catch (Exception e) {
                // Failed to load this player's NBT data :v
                RandomEvents.logger.error("Failed to load player NBT data for UUID " + playerUuidStr + "! " + e.getMessage());
            }
        }

        return nbtMap;
    }

    public NBTTagCompound writePlayerNbtMap(NBTTagCompound nbt, HashMap<UUID, NBTTagCompound> nbtMap)
    {
        // Get/create players compound tag
        if (!nbt.hasKey("players"))
            nbt.setTag("players", new NBTTagCompound());
        NBTTagCompound playersNbt = nbt.getCompoundTag("players");

        for (Map.Entry<UUID, NBTTagCompound> playerNbt : nbtMap.entrySet()) {
            playersNbt.setTag(playerNbt.getKey().toString(), playerNbt.getValue());
        }

        return nbt;
    }

    /**
     * Alias for <code>worldSavedData.markDirty()</code>.
     */
    public final void markDirty()
    {
        worldSavedData.markDirty();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (DimensionManager.getWorld(0) == event.getWorld()) {
            RandomEvents.logger.info("Loading Random Events NBT data...");

            World world = event.getWorld();

            // Load WorldSavedData
            worldSavedData = (RandomEventsWorldSavedData) world.getPerWorldStorage().getOrLoadData(RandomEventsWorldSavedData.class, tagname);
            if (worldSavedData == null) {
                worldSavedData = new RandomEventsWorldSavedData(tagname);
                world.getPerWorldStorage().setData(tagname, worldSavedData);
                worldSavedData.markDirty();
            }
        }
    }
}
