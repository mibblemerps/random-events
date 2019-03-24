package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.MobSpawner;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventInvisibleSpiders extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    private int minSpiderInvisibilityTime = 90;
    private int maxSpiderInvisibilityTime = 180;

    public RandomEventInvisibleSpiders()
    {
        super("invisible_spiders");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(1.5), TimeHelper.hrsToSecs(3.5)));
        addComponent(new CPlayerEvent());

        // Load config
        minSpiderInvisibilityTime = RandomEvents.config.get(getConfigName(), "minimum_spider_invisibility_time", minSpiderInvisibilityTime,
                "Minimum time a spider will stay invisible - in seconds.").getInt();
        maxSpiderInvisibilityTime = RandomEvents.config.get(getConfigName(), "maximum_spider_invisibility_time", maxSpiderInvisibilityTime,
                "Maximum time a spider will stay invisible - in seconds.").getInt();
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        MobSpawner.MobSpawnEventParameters parameters = new MobSpawner.MobSpawnEventParameters(8, 12, 22);
        MobSpawner.execute(this, parameters, world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntitySpider spider = new EntitySpider(world);
        spider.setRevengeTarget(player);

        int invisibilityTimeTicks = ThreadLocalRandom.current().nextInt(minSpiderInvisibilityTime, maxSpiderInvisibilityTime) * 20;
        spider.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, invisibilityTimeTicks, 0));

        return spider;
    }
}
