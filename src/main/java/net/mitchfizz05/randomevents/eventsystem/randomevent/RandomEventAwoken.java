package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.MobSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventAwoken extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    private boolean enableFixedSleepTime = false;
    private int sleepTime = 8000;

    private float minChance = 0.03f;
    private float minChanceBeforeGold = 0.1f;
    private float baseChance = 0.2f;
    private boolean skullsIncreaseChance = true;
    private boolean goldBlocksDecreaseChance = true;

    public RandomEventAwoken()
    {
        super("awoken");

        addComponent(new CPlayerEvent());

        MinecraftForge.EVENT_BUS.register(this);

        // Load config
        minChance = (float) RandomEvents.config.get(getConfigName(), "min_chance", minChance,
                "Minimum chance of being awoken regardless of all other factors.").getDouble();
        minChanceBeforeGold = (float) RandomEvents.config.get(getConfigName(), "min_chance_before_gold", minChanceBeforeGold,
                "Minimum chance of being awoken before gold blocks are calculated.").getDouble();
        baseChance = (float) RandomEvents.config.get(getConfigName(), "base_chance", baseChance,
                "Magic base chance number that determines how common being awoken is. Default is 0.2").getDouble();
        skullsIncreaseChance = RandomEvents.config.get(getConfigName(), "skulls_increase_chance", skullsIncreaseChance,
                "Should placing skulls around a bed will increase the chance of being awoken greatly.").getBoolean();
        goldBlocksDecreaseChance = RandomEvents.config.get(getConfigName(), "gold_blocks_decrease_chance", skullsIncreaseChance,
                "Should placing gold blocks around a bed decrease the chance of being awoken.").getBoolean();
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            throw new ExecuteEventException("Cannot execute in peaceful mode", this);
        }

        if (!player.isPlayerSleeping()) {
            throw new ExecuteEventException("Player not sleeping", this);
        }

        player.wakeUpPlayer(true, false, true);

        MobSpawner.execute(this, new MobSpawner.MobSpawnEventParameters(1, 3, 2), world, player);
    }

    @SubscribeEvent
    public void OnTick(TickEvent.PlayerTickEvent playerTickEvent)
    {
        EntityPlayer player = playerTickEvent.player;

        if (player.world.isRemote) return;

        if (player.isPlayerFullyAsleep()) {
            if (ThreadLocalRandom.current().nextFloat() < calculateChance(player.world, player)) {
                // Trigger event
                try {
                    RandomEventServices.executeEventService.executeEvent(this, player.world, player);
                    return;
                } catch (ExecuteEventException ignored) {}
            }

            if (enableFixedSleepTime) {
                // Simply advance the time forward a bit.
                player.world.setWorldTime(player.world.getWorldTime() + sleepTime);
                player.wakeUpPlayer(true, false, true);
            }
        }
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        float f = ThreadLocalRandom.current().nextFloat();

        EntityLiving entity;

        if (f < 0.2f) {
            entity = new EntitySkeleton(world);
        } else if (f < 0.4f) {
            entity = new EntityZombie(world);
        } else if (f < 0.6f) {
            entity = new EntitySpider(world);
        } else {
            entity = new EntityCreeper(world);
        }

        if (ThreadLocalRandom.current().nextBoolean()) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 60 * 20, ThreadLocalRandom.current().nextInt(0, 2)));
        }

        return entity;
    }

    private float calculateChance(World world, EntityPlayer player)
    {
        float chance = baseChance;

        BlockPos sleepPos = player.getPosition();

        // Increase based on current light level
        float lightLevel = world.getLightBrightness(sleepPos.add(0, 1, 0));
        lightLevel = (float) Math.log(lightLevel + 1);
        lightLevel = Math.max(lightLevel, 0.05f);
        chance /= (lightLevel * 16);

        if (world.canSeeSky(sleepPos.add(0, 1, 0))) {
            chance *= 1.8f;
        }

        if (skullsIncreaseChance) {
            CoordinateHelper.BlockScanResult[] result = CoordinateHelper.scanForBlocks(world, Blocks.SKULL, sleepPos, 3);
            chance *= 1 + (result.length * 0.85f);
        }

        // Apply minimum chance for before gold
        chance = Math.max(chance, minChanceBeforeGold);

        if (goldBlocksDecreaseChance) {
            CoordinateHelper.BlockScanResult[] result = CoordinateHelper.scanForBlocks(world, Blocks.GOLD_BLOCK, sleepPos, 3);
            chance /= 1 + (result.length * 1.25f);
        }

        // Apply minimum chance
        chance = Math.max(chance, minChance);

        RandomEvents.logger.info(player.getName() + ": Chance of being awoken: " + chance);

        return chance;
    }
}
