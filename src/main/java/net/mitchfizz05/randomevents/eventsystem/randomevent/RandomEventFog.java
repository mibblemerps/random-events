package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IEventTick;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RandomEventFog extends RandomEvent implements IEventTick
{
    protected CLongTimedEvent longTimedEvent;

    // Min/max seconds this event can run for
     protected int minTime = 300;
     protected int maxTime = 800;

     // Seconds it takes to fade in/out of the fog
     protected double fadeInTime = 10;
     protected double fadeOutTime = 4;

    /**
     * How dense the fog should be.
     */
    protected double fogDensity = 0.25;

    public RandomEventFog()
    {
        super("fog");

        addComponent(new CWorldTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(2)));
        longTimedEvent = (CLongTimedEvent) addComponent(new CLongTimedEvent());

        MinecraftForge.EVENT_BUS.register(this);

        fogDensity = RandomEvents.config.get(getConfigName(), "fog_density", fogDensity,
                "How dense the fog should be. 0.25 is a reasonable value for heavy fog.",
                0, 10).getDouble();

        fadeInTime = RandomEvents.config.get(getConfigName(), "fade_in_seconds", fadeInTime,
                "How many seconds it should take for fog to ramp up to full density.",
                0, 10000).getDouble();

        fadeOutTime = RandomEvents.config.get(getConfigName(), "fade_out_seconds", fadeInTime,
                "How many seconds it should take for fog to clear out.",
                0, 10000).getDouble();
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        longTimedEvent.timeLeft = ThreadLocalRandom.current().nextInt(minTime, maxTime);
    }

    @Override
    public void tick(@Nonnull World world, @Nullable EntityPlayer player)
    {
        //Minecraft.getMinecraft().entityRenderer.clo
    }

    @SubscribeEvent
    public void onGetFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        if (longTimedEvent.isActive()) {
            double d = fogDensity;

            if (longTimedEvent.timeElapsed <= fadeInTime) {
                // Fade in
                d = MathHelper.clampedLerp(0, d, longTimedEvent.timeElapsed / fadeInTime);
            } else if (longTimedEvent.timeLeft <= fadeOutTime) {
                // Fade out
                d = MathHelper.clampedLerp(0, d, longTimedEvent.timeElapsed / fadeOutTime);
            }

            event.setDensity((float) d);
            event.setCanceled(true);
        }
    }
}
