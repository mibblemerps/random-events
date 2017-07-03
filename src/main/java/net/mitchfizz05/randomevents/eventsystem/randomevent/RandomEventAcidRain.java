package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IEventTick;
import net.mitchfizz05.randomevents.eventsystem.component.CLongTimedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Acidic rain that hurts all that stands in it.
 */
public class RandomEventAcidRain extends RandomEvent implements IEventTick
{
    public RandomEventAcidRain()
    {
        super("acid_rain");

        addComponent(new CLongTimedEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        CLongTimedEvent cLongTimedEvent = (CLongTimedEvent) getComponent(CLongTimedEvent.class);
        cLongTimedEvent.timeLeft = 5; // lasts for 5 seconds
    }

    @Override
    public void tick(@Nonnull World world, @Nullable EntityPlayer player)
    {
        //
    }
}
