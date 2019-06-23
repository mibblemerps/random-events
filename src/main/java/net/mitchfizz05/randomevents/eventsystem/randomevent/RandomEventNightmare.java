package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.item.ModItems;
import net.mitchfizz05.randomevents.util.TeleportHelper;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructure;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructures;
import scala.tools.nsc.interpreter.Naming;

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
    private static final int nightmareSpacing = 128;

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
        BlockPos pos = getNightmareStructureStartPos(currentIndex);

        // Generate!
        RandomEvents.logger.info("Generating nightmare " + nightmareStructure.getId()
                + " for " + player.getName() + " at " + pos.toString() + "...");
        nightmareStructure.placeIntoWorld(dreamRealm, player, pos);

        RandomEvents.logger.info("Structure generated, teleporting player...");

        // Save player nightmare data
        NBTTagList inventory = player.inventory.writeToNBT(new NBTTagList());
        playerDataMap.put(player.getUniqueID(), new PlayerData(currentIndex, nightmareType,
                (float) player.posX, (float) player.posY, (float) player.posZ,
                player.cameraYaw, player.cameraPitch, player.dimension,
                player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel(),
                inventory));

        physicallyEnterNightmare(player);

        // We've updated NBT stuff so mark it dirty
        RandomEventServices.nbtService.markDirty();

        player.sendMessage(new TextComponentString(nightmareStructure.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.RED)));

        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20 * 6, 0, true, false));
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

        BlockPos nightmareStartPos = getNightmareStructureStartPos(playerNightmareIndex);
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
            escapePlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent tickEvent)
    {
        EntityPlayer player = tickEvent.player;

        if (player.world.isRemote) return;

        if (tickEvent.player.world.getTotalWorldTime() % 100 == 0)
        {
            destroyForbiddenItems(player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent respawnEvent)
    {
        EntityPlayer player = respawnEvent.player;

        if (player.world.isRemote) return;

        if (!playerDataMap.containsKey(player.getUniqueID())) return;

        physicallyEnterNightmare(player);
    }

    /**
     * Take player out of the nightmare state.
     */
    public void escapePlayer(EntityPlayer player)
    {
        if (!playerDataMap.containsKey(player.getUniqueID())) return;

        PlayerData playerData = playerDataMap.remove(player.getUniqueID());
        RandomEventServices.nbtService.markDirty();

        destroyForbiddenItems(player);

        // Teleport player back to where they were before entering the nightmare
        TeleportHelper.teleport(player, playerData.lastX, playerData.lastY, playerData.lastZ,
                playerData.lastYaw, playerData.lastPitch, playerData.lastDim);

        // Reset player stats to what they were before entering nightmare
        player.setHealth(playerData.lastHealth);
        player.getFoodStats().setFoodLevel(playerData.lastHunger);
        player.getFoodStats().setFoodSaturationLevel(playerData.lastSaturation);

        // Reload inventory
        player.inventory.readFromNBT(playerData.inventory);

        // Clear potion effects
        player.clearActivePotions();

        player.setGameType(GameType.SURVIVAL);
    }

    private void physicallyEnterNightmare(EntityPlayer player)
    {
        PlayerData playerData = playerDataMap.get(player.getUniqueID());

        BlockPos startPos = getNightmareStructureStartPos(playerData.nightmareIndex);
        NightmareStructure nightmareStructure = NightmareStructures.nightmareStructures.get(playerData.nightmareType);

        Vec3d relativeSpawnPos = nightmareStructure.getSpawnPos();
        Vec3d spawnPos = relativeSpawnPos.add(new Vec3d(startPos.getX(), startPos.getY(), startPos.getZ()));

        TeleportHelper.teleport(player, (float) spawnPos.x, (float) spawnPos.y, (float) spawnPos.z, 0, 0, Dimensions.DREAMREALM.getId());

        // Adventure mode!
        player.setGameType(GameType.ADVENTURE);

        // Clear inventory
        player.inventory.clear();

        // Heal player and give food
        player.heal(20);
        player.getFoodStats().setFoodLevel(20);
        player.getFoodStats().setFoodSaturationLevel(1f);

        player.clearActivePotions();

        // Give sword
        player.inventory.addItemStackToInventory(new ItemStack(ModItems.dreamSword, 1));
    }

    private void destroyForbiddenItems(EntityPlayer player)
    {
        if (playerDataMap.containsKey(player.getUniqueID())) return;

        // Check for forbidden items outside of nightmares
        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack.getItem() instanceof IDreamItem)
            {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null){
                    if (tag.hasKey("Keep") && tag.getBoolean("Keep")) {
                        continue; // Keep this item
                    }
                }

                // Destroy forbidden item
                stack.setCount(0);
            }
        }
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
            playerNbt.setFloat("last_health", playerData.getValue().lastHealth);
            playerNbt.setInteger("last_hunger", playerData.getValue().lastHunger);
            playerNbt.setFloat("last_saturation", playerData.getValue().lastSaturation);
            playerNbt.setTag("inventory", playerData.getValue().inventory);

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
                    playerNbt.getInteger("last_dim"),
                    playerNbt.getFloat("last_health"),
                    playerNbt.getInteger("last_hunger"),
                    playerNbt.getFloat("last_saturation"),
                    playerNbt.getTagList("inventory", 0));

            if (data.nightmareIndex < 0) continue;

            playerDataMap.put(playerEntry.getKey(), data);
        }
    }

    public static BlockPos getNightmareStructureStartPos(int nightmareIndex)
    {
        return new BlockPos(nightmareIndex * nightmareSpacing, 64, 0);
    }

    /**
     * Any item that implements this will be destroyed when a player isn't having a nightmare
     */
    public interface IDreamItem {}

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

        public float lastHealth;
        public int lastHunger;
        public float lastSaturation;

        public NBTTagList inventory;

        public PlayerData(int nightmareIndex, String nightmareType,
                          float lastX, float lastY, float lastZ,
                          float lastYaw, float lastPitch, int lastDim,
                          float lastHealth, int lastHunger, float lastSaturation, NBTTagList inventory)
        {
            this.nightmareIndex = nightmareIndex;
            this.nightmareType = nightmareType;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
            this.lastYaw = lastYaw;
            this.lastPitch = lastPitch;
            this.lastDim = lastDim;
            this.lastHealth = lastHealth;
            this.lastHunger = lastHunger;
            this.lastSaturation = lastSaturation;
            this.inventory = inventory;
        }
    }
}

