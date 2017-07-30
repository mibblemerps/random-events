package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IEventTick;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A super storm where lightning strikes everywhere (including the player ;P)
 */
public class RandomEventSuperStorm extends RandomEvent implements IEventTick
{
    protected List<EntityPlayerMP> players;

    protected int baseFreqency = 25;
    protected float strikeChance = 0.15f;

    public RandomEventSuperStorm()
    {
        super("super_storm");

        addComponent(new CWorldTimer(this, TimeHelper.hrsToSecs(1.5), TimeHelper.hrsToSecs(2)));
        addComponent(new CLongTimedEvent());
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        CLongTimedEvent cLongTimedEvent = (CLongTimedEvent) getComponent(CLongTimedEvent.class);
        if (cLongTimedEvent.isActive()) throw new ExecuteEventException("Super storm already active", this);

        cLongTimedEvent.timeLeft = ThreadLocalRandom.current().nextInt(20, 45 - 1);
        RandomEventServices.nbtService.markDirty();
    }

    @Override
    public void tick(@Nonnull World world, @Nullable EntityPlayer player)
    {
        // Update weather
        WorldInfo worldInfo = world.getWorldInfo();
        worldInfo.setCleanWeatherTime(0);
        worldInfo.setThundering(true);
        worldInfo.setThunderTime(20 * 10);
        worldInfo.setRaining(true);
        worldInfo.setRainTime(20 * 10);

        // Get all players
        try {
            players = world.getMinecraftServer().getPlayerList().getPlayers();
            if (players.size() == 0) return; // No players
        } catch (NullPointerException e) {
            return; // Couldn't get player list
        }

        // Every so often, spawn a lightning bolt near each online player.
        spawnExtraLightning(world);

        // Strike a player with lightning every so often :3
        strikePlayers(world);
    }

    /**
     * Spawn extra lightning around the players.
     * Frequency is configurable.
     *
     * @param world World
     */
    public void spawnExtraLightning(World world)
    {
        // Calculate a frequency weighted by number of players online. This ensures a relatively even amount of
        // lightning per player regardless of how many's logged in.
        int freq = baseFreqency / players.size(); // default base: 25

        if (world.getWorldInfo().getWorldTotalTime() % freq != 0) return;

        // Pick a player (at random)
        EntityPlayerMP player = players.get(ThreadLocalRandom.current().nextInt(0, players.size()));

        // Determine where to spawn lightning
        BlockPos lightningPos = CoordinateHelper.getCoordinateAround(player.getPosition(), 64);
        lightningPos = world.getTopSolidOrLiquidBlock(lightningPos);

        // Spawn the lightning
        EntityLightningBolt bolt = new EntityLightningBolt(world, 0d, 0d, 0d, false);
        bolt.setLocationAndAngles(lightningPos.getX(), lightningPos.getY(), lightningPos.getZ(), 0f, 0f);
        world.addWeatherEffect(bolt);
        MinecraftServer server = world.getMinecraftServer();
    }

    /**
     * Strike some unlucky players.
     * Runs every 5 seconds. Chance is configurable.
     * By default: 15% of player being struck every 5 seconds. If you're outside for 30 seconds, you have a 90% of being hit.
     *
     * @param world World
     */
    public void strikePlayers(World world)
    {
        if (world.getWorldInfo().getWorldTotalTime() % 20 * 5 != 0) return; // Only strike players every 5 seconds

        for (EntityPlayerMP player : players) {
            if (!world.canSeeSky(player.getPosition())) {
                continue;
            }

            if (ThreadLocalRandom.current().nextFloat() <= strikeChance) { // default: 0.15f
                // Strike them >:D
                EntityLightningBolt bolt = new EntityLightningBolt(world, 0d, 0d, 0d, false);
                bolt.setLocationAndAngles(player.posX, player.posY, player.posZ, 0f, 0f);
                world.addWeatherEffect(bolt);
            }
        }
    }
}
