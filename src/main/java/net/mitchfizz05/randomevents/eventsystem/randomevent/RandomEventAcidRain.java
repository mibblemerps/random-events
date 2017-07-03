package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.content.REDamageSources;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IEventTick;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Acidic rain that hurts all that stands in it.
 */
public class RandomEventAcidRain extends RandomEvent implements IEventTick
{
    protected int acidRainMinTime = 120;
    protected int acidRainMaxTime = 180;

    public RandomEventAcidRain()
    {
        super("acid_rain");

        addComponent(new CWorldTimer(this, 600, 1200));
        addComponent(new CLongTimedEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        CLongTimedEvent cLongTimedEvent = (CLongTimedEvent) getComponent(CLongTimedEvent.class);
        cLongTimedEvent.timeLeft = ThreadLocalRandom.current().nextInt(acidRainMinTime, acidRainMaxTime);
    }

    @Override
    public void tick(@Nonnull World world, @Nullable EntityPlayer player)
    {
        // Set rain
        WorldInfo worldInfo = world.getWorldInfo();
        worldInfo.setCleanWeatherTime(0);
        worldInfo.setThunderTime(0);
        worldInfo.setRainTime(20 * 10);
        worldInfo.setRaining(true);
        worldInfo.setThundering(false);

        // Every second, hurt all entities.
        if (world.getTotalWorldTime() % 20 == 0) {
            // Assemble list of entities to hurt (we don't hurt them inside this loop, because it causes concurrent modification exceptions)
            List<Entity> entities = world.getLoadedEntityList();
            List<EntityLivingBase> entitiesToHurt = new ArrayList<EntityLivingBase>();
            for (Entity entity : entities) {
                if (entity instanceof EntityLivingBase) {
                    entitiesToHurt.add((EntityLivingBase)entity);
                }
            }

            for (EntityLivingBase entityLiving : entitiesToHurt) {
                // Check if not exposed to sky.
                BlockPos pos = new BlockPos(entityLiving.getPosition().getX(),
                        entityLiving.getPosition().getY() + 1,
                        entityLiving.getPosition().getZ());
                if (!world.isRainingAt(pos)) continue;

                // Hurt entity.
                entityLiving.attackEntityFrom(REDamageSources.acidRain, 2f);
            }
        }
    }
}
