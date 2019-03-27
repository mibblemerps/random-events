package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IUsesNBT;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.util.SimpleTeleporter;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructure;
import net.mitchfizz05.randomevents.world.worldgen.nightmares.NightmareStructures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventNightmare extends RandomEvent implements IUsesNBT
{
    private int nightmareIndex = 0;

    /**
     * Physical spacing between nightmare structures in the dream realm.
     */
    private final int nightmareSpacing = 128;

    public RandomEventNightmare()
    {
        super("nightmare");
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        // Get the dream realm world
        WorldServer dreamRealm = world.getMinecraftServer().getWorld(Dimensions.DREAMREALM.getId());

        // Pick a nightmare
        int i = ThreadLocalRandom.current().nextInt(0, NightmareStructures.nightmareStructures.size());
        NightmareStructure nightmareStructure = NightmareStructures.nightmareStructures.get(i);

        // Select location
        BlockPos pos = new BlockPos((nightmareIndex++) * nightmareSpacing, 64, 0);
        RandomEventServices.nbtService.markDirty(); // mark nbt as dirty so the new nightmare index can be saved

        // Generate!
        RandomEvents.logger.info("Generating nightmare " + nightmareStructure.getId()
                + " for " + player.getName() + " at " + pos.toString() + "...");
        nightmareStructure.placeIntoWorld(dreamRealm, player, pos);

        RandomEvents.logger.info("Structure generated, teleporting player...");

        // Teleport player
        BlockPos relativeSpawnPos = nightmareStructure.getSpawnPos();
        BlockPos spawnPos = pos.add(relativeSpawnPos.getX(), relativeSpawnPos.getY(), relativeSpawnPos.getZ());
        player.changeDimension(dreamRealm.provider.getDimension(), new SimpleTeleporter(dreamRealm));
        player.cameraYaw = 0;
        player.cameraPitch = -45;
        player.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        player.sendMessage(new TextComponentString(nightmareStructure.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.RED)));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("nightmare_index", nightmareIndex);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        nightmareIndex = nbt.getInteger("nightmare_index");
    }
}
