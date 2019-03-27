package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.util.SimpleTeleporter;
import net.mitchfizz05.randomevents.util.TeleportHelper;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructure;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventNightmare extends RandomEvent implements IUsesNBT
{
    private int nightmareIndex = 0;

    private HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Physical spacing between nightmare structures in the dream realm.
     */
    private final int nightmareSpacing = 128;

    public RandomEventNightmare()
    {
        super("nightmare");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Get the dream realm world
        WorldServer dreamRealm = world.getMinecraftServer().getWorld(Dimensions.DREAMREALM.getId());

        // Pick a nightmare
        Object[] nightmareStructureArray = NightmareStructures.nightmareStructures.values().toArray();
        NightmareStructure nightmareStructure = (NightmareStructure) nightmareStructureArray[ThreadLocalRandom.current().nextInt(0, nightmareStructureArray.length)];
        String nightmareType = nightmareStructure.getId();

        // Select location
        int currentIndex = nightmareIndex++;
        BlockPos pos = new BlockPos(currentIndex * nightmareSpacing, 64, 0);

        // Generate!
        RandomEvents.logger.info("Generating nightmare " + nightmareStructure.getId()
                + " for " + player.getName() + " at " + pos.toString() + "...");
        nightmareStructure.placeIntoWorld(dreamRealm, player, pos);

        RandomEvents.logger.info("Structure generated, teleporting player...");

        // Save player nightmare data
        playerDataMap.put(player.getUniqueID(), new PlayerData(currentIndex, nightmareType,
                (float) player.posX, (float) player.posY, (float) player.posZ,
                player.cameraYaw, player.cameraPitch, player.dimension));

        // Teleport player
        Vec3d relativeSpawnPos = nightmareStructure.getSpawnPos();
        Vec3d spawnPos = relativeSpawnPos.add(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
        TeleportHelper.teleport(player, (float) spawnPos.x, (float) spawnPos.y, (float) spawnPos.z,
                0, 0, dreamRealm.provider.getDimension());

        // We've updated NBT stuff so mark it dirty
        RandomEventServices.nbtService.markDirty();

        player.sendMessage(new TextComponentString(nightmareStructure.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.RED)));
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent sleepEvent)
    {
        if (sleepEvent.getEntity().world.isRemote) return;

        EntityPlayer player = sleepEvent.getEntityPlayer();

        // Must be in the dream dimension
        if (player.dimension != Dimensions.DREAMREALM.getId()) return;

        int playerNightmareIndex = getPlayerNightmareIndex(player);
        if (playerNightmareIndex < 0) return;

        sleepEvent.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);

        BlockPos nightmareStartPos = new BlockPos(playerNightmareIndex * nightmareSpacing, 64, 0);
        //BlockPos sleepRelativePos = sleepEvent.getPos().add(-nightmareStartPos.getX(), -nightmareStartPos.getY(), -nightmareStartPos.getZ());
        //BlockPos sleepRelativePos = nightmareStartPos.subtract(new Vec3i(sleepEvent.getPos().getX(), sleepEvent.getPos().getY(), sleepEvent.getPos().getZ()));
        BlockPos sleepRelativePos = new BlockPos(
                sleepEvent.getPos().getX() - nightmareStartPos.getX(),
                sleepEvent.getPos().getY() - nightmareStartPos.getY(),
                sleepEvent.getPos().getZ() - nightmareStartPos.getZ());

        RandomEvents.logger.debug("Sleep pos: " + sleepEvent.getPos() + " | Relative: " + sleepRelativePos);

        NightmareStructure nightmareStructure = getPlayerNightmareStructure(player);

        BlockPos actualEndBedPos = nightmareStructure.getEndBedPos();
        if (sleepRelativePos.equals(actualEndBedPos))
        {
            // Player escaped!
            EscapePlayer(player);
        }
    }

    /**
     * Take player out of the nightmare state.
     */
    public void EscapePlayer(EntityPlayer player)
    {
        if (!playerDataMap.containsKey(player.getUniqueID())) return;

        PlayerData playerData = playerDataMap.remove(player.getUniqueID());

        // Teleport player back to where they were before entering the nightmare
        TeleportHelper.teleport(player, playerData.lastX, playerData.lastY, playerData.lastZ,
                playerData.lastYaw, playerData.lastPitch, playerData.lastDim);
    }

    /**
     * Get players nightmare index. If they're not in a nightmare, this will return -1.
     */
    private int getPlayerNightmareIndex(EntityPlayer player)
    {
        if (!playerDataMap.containsKey(player.getUniqueID())) return -1;

        int index = playerDataMap.get(player.getUniqueID()).nightmareIndex;
        if (index < 0) return -1;
        return index;
    }

    private NightmareStructure getPlayerNightmareStructure(EntityPlayer player)
    {
        if (!playerDataMap.containsKey(player.getUniqueID())) return null;

        return NightmareStructures.nightmareStructures.get(playerDataMap.get(player.getUniqueID()).nightmareType);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("nightmare_index", nightmareIndex);

        // Write player specific data to NBT
        HashMap<UUID, NBTTagCompound> playerNbtMap = new HashMap<>();
        for (HashMap.Entry<UUID, PlayerData> playerData : playerDataMap.entrySet())
        {
            NBTTagCompound playerNbt = new NBTTagCompound();
            playerNbt.setInteger("nightmare_index", playerData.getValue().nightmareIndex);
            playerNbt.setString("nightmare_type", playerData.getValue().nightmareType);
            playerNbt.setFloat("last_x", playerData.getValue().lastX);
            playerNbt.setFloat("last_y", playerData.getValue().lastY);
            playerNbt.setFloat("last_z", playerData.getValue().lastZ);
            playerNbt.setFloat("last_yaw", playerData.getValue().lastYaw);
            playerNbt.setFloat("last_pitch", playerData.getValue().lastPitch);
            playerNbt.setInteger("last_dim", playerData.getValue().lastDim);

            playerNbtMap.put(playerData.getKey(), playerNbt);
        }
        RandomEventServices.nbtService.writePlayerNbtMap(nbt, playerNbtMap);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        nightmareIndex = nbt.getInteger("nightmare_index");

        // Load player specific data from NBT
        playerDataMap.clear();
        for (HashMap.Entry<UUID, NBTTagCompound> playerEntry : RandomEventServices.nbtService.getPlayersNbtMap(nbt).entrySet())
        {
            NBTTagCompound playerNbt = playerEntry.getValue();

            PlayerData data = new PlayerData(
                    playerNbt.getInteger("nightmare_index"),
                    playerNbt.getString("nightmare_type"),
                    playerNbt.getFloat("last_x"),
                    playerNbt.getFloat("last_y"),
                    playerNbt.getFloat("last_z"),
                    playerNbt.getFloat("last_yaw"),
                    playerNbt.getFloat("last_pitch"),
                    playerNbt.getInteger("last_dim"));


            if (data.nightmareIndex < 0) continue;

            playerDataMap.put(playerEntry.getKey(), data);
        }
    }

    private class PlayerData
    {
        public int nightmareIndex;
        public String nightmareType;

        public float lastX;
        public float lastY;
        public float lastZ;
        public float lastYaw;
        public float lastPitch;
        public int lastDim;

        public PlayerData(int nightmareIndex, String nightmareType, float lastX, float lastY, float lastZ, float lastYaw, float lastPitch, int lastDim)
        {
            this.nightmareIndex = nightmareIndex;
            this.nightmareType = nightmareType;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
            this.lastYaw = lastYaw;
            this.lastPitch = lastPitch;
            this.lastDim = lastDim;
        }
    }
}
